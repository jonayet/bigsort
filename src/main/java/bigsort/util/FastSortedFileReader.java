package bigsort.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import bigsort.util.api.LineReader;
import bigsort.util.api.SortedFileReader;

/**
 * FastBatchFileReader
 */
public class FastSortedFileReader implements SortedFileReader {
    private LineReader lineReader;
    private Map<Integer, LineReader> readers;
    private int batchSize;
    private PriorityBlockingQueue<Line> queue;
    private Line minLine = null;

    /**
     * This comparator is used to sort 2 line
     */
    private static final Comparator<Line> comparator = (l1, l2) -> {
        if (l1.value().isEmpty() || l2.value().isEmpty()) {
            return 0;
        }
        return l1.value().get().compareTo(l2.value().get());
    };

    public FastSortedFileReader(LineReader lineReader) {
        this.lineReader = lineReader;
    }

    @Override
    public void initialize(List<Path> files, int batchSize) throws IOException {
        this.batchSize = Math.min(batchSize, files.size());
        this.queue = new PriorityBlockingQueue<>(this.batchSize, comparator);
        this.readers = createReaders(files);
    }

    /**
     * Geting next ascending line can be achived in two ways. Case 1: batchSize >=
     * files.size(), read out the min value from Priority queue and read next line
     * of the respective file. For first time, all first lines need to read and add
     * to priority queue. Case 2: batchSize < files.size(), read all current lines
     * backed by lineReaders and get the min one Case 1 is much more efficiency than
     * Case 2.
     */
    @Override
    public Optional<String> readNextSortedLine() throws IOException {
        synchronized (this) {
            if (!hasMoreToRead()) {
                return Optional.empty();
            }

            if (batchSize >= readers.size()) {
                return pollAndReadNextFile();
            }

            return readEachLineAndGetMin();
        }
    }

    /**
     * This function should be called when batchSize >= files.size() It will use the
     * PriorityQueue to get the most acending line among all lines pointed by
     * lineReaders
     * 
     * @throws IOException
     */
    private Optional<String> pollAndReadNextFile() throws IOException {
        // first time read all first lines from all file
        if (queue.size() == 0) {
            readEachLine(this::addLineToQueue);
        }

        // still nothing in queue?
        if (queue.size() == 0) {
            return Optional.empty();
        }

        // extract min value line and set pointer to next for respective file
        Line line = queue.poll();

        // read next line of current file and add to queue
        int fileNo = line.fileNo();
        var reader = readers.get(fileNo);
        Line nextLine = new Line(fileNo, reader.next());
        addLineToQueue(nextLine);

        return line.value();
    }

    /**
     * This function should be called when batchSize < files.size() It will not use
     * the PriorityQueue
     * 
     * @throws IOException
     */
    private Optional<String> readEachLineAndGetMin() throws IOException {
        minLine = null;
        readEachLine(this::keepMinLine);
        if (minLine == null) {
            return Optional.empty();
        }

        var reader = readers.get(minLine.fileNo());
        reader.next();
        return minLine.value();
    }

    /**
     * read each line specified by lineMarker[id]
     * 
     * @throws IOException
     */
    private void readEachLine(Consumer<Line> onRead) throws IOException {
        for (int i = 0; i < readers.size(); i++) {
            var reader = readers.get(i);
            onRead.accept(new Line(i, reader.line()));
        }
    }

    @Override
    public Stream<String> readAllSortedLines() {
        Spliterator<String> spliterator = new Spliterator<String>() {
            @Override
            public boolean tryAdvance(Consumer<? super String> action) {
                Optional<String> value;
                try {
                    value = readNextSortedLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                value.ifPresent(action::accept);
                return value.isPresent();
            }

            @Override
            public Spliterator<String> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }

            @Override
            public int characteristics() {
                return Spliterator.IMMUTABLE;
            }
        };

        return StreamSupport.stream(spliterator, false);
    }

    private void addLineToQueue(Line line) {
        if (line.value().isEmpty()) {
            return;
        }
        queue.add(line);
    }

    private void keepMinLine(Line line) {
        if (line.value().isEmpty()) {
            return;
        }

        if (minLine == null) {
            minLine = line;
            return;
        }

        if (comparator.compare(line, minLine) < 0) {
            minLine = line;
        }
    }

    private Map<Integer, LineReader> createReaders(List<Path> files) throws IOException {
        Map<Integer, LineReader> readers = new HashMap<>();
        int id = 0;
        for (Path path : files) {
            readers.put(id++, lineReader.create(path));
        }
        return readers;
    }

    private boolean hasMoreToRead() throws IOException {
        for (int i = 0; i < readers.size(); i++) {
            var reader = readers.get(i);
            if (reader.line().isPresent()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() {
        for (var reader : readers.values()) {
            reader.close();
        }
    }
}
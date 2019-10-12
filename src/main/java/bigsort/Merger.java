package bigsort;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import bigsort.exceptions.BigSortException;
import bigsort.util.StreamUtils;
import bigsort.util.api.FileWriter;
import bigsort.util.api.SortedFileReader;

/**
 * Merger is responsible for reading all the split files and append chunk of
 * sorted lines to output file specified by "destPath". It will use
 * SortedFileReader to get ascending ordered line stream from sorted split
 * files. StreamUtils will be used to buffer the stream and FileWrite will be
 * used to append the lines to output file.
 */
public class Merger {
    private final SortedFileReader sortedFileReader;
    private final FileWriter fileWriter;
    private final StreamUtils streamUtils;
    private Path destPath;
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public Merger(SortedFileReader sortedFileReader, FileWriter fileWriter, StreamUtils streamUtils) {
        this.sortedFileReader = sortedFileReader;
        this.fileWriter = fileWriter;
        this.streamUtils = streamUtils;
    }

    public void merge(List<Path> splitFiles, Params params) {
        this.destPath = params.getDestPath();
        // read every split files and append lines in sorted order to destPath file
        openSortedLineStream(splitFiles, params.getK(), onSortedData);

        // close readers
        sortedFileReader.close();
    }

    BiConsumer<String[], Integer> onSortedData = (data, batchId) -> {
        appendToOutput(destPath, data, batchId);
    };

    private void openSortedLineStream(List<Path> paths, int batchSize, BiConsumer<String[], Integer> onSortedData) {
        logger.log(Level.INFO, "Open sorted line stream for {0} files", new Object[] { paths.size() });

        try {
            sortedFileReader.initialize(paths, batchSize);
        } catch (IOException e) {
            throw new BigSortException("Error occured while initializing sortedFileReader", e);
        }

        try (Stream<String> stream = sortedFileReader.readAllSortedLines()) {
            streamUtils.processInBatch(stream, batchSize, (buffer, batchId) -> {
                onSortedData.accept(buffer, batchId);
            });
        }
    }

    private void appendToOutput(Path path, String[] data, int batchId) {
        try {
            fileWriter.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            logger.log(Level.FINE, "Batch {0}: {1} lines appended to path: {2}",
                    new Object[] { batchId, data.length, path.toString() });
        } catch (IOException e) {
            throw new BigSortException(String.format("Error occured while appending to %s", path.toString()), e);
        }
    }
}
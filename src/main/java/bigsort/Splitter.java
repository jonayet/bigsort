package bigsort;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import bigsort.exceptions.BigSortException;
import bigsort.util.api.FileReader;
import bigsort.util.api.FileWriter;
import bigsort.util.api.Sorter;
import bigsort.util.StreamUtils;

/**
 * Spliter is reponsible for reading the source file or files and to split them
 * to small chunk of sorted files. It will use FileReader to produce a single
 * stream of Strings(lines) regardless of if srcPath is a file or directory. And
 * StreamUtils will help to chunk the stream data into a buffer of "k" length.
 * Then each chunk of k lines will be sorted in ascending order and will be
 * saved as split files inside the "workDir" folder.
 */
public class Splitter {
    private final FileReader fileReader;
    private final FileWriter fileWriter;
    private final StreamUtils streamUtils;
    private final String splitFileExtension = ".txt";
    private final int MAX_SPLIT_SIZE = 1_000_000;
    private Sorter sorter;
    private Path workDir;
    private List<Path> splitFiles;
    final Logger logger = Logger.getLogger(App.class.getName());

    public Splitter(FileReader fileReader, FileWriter fileWriter, Sorter sorter, StreamUtils streamUtils) {
        this.fileReader = fileReader;
        this.fileWriter = fileWriter;
        this.sorter = sorter;
        this.streamUtils = streamUtils;
        this.splitFiles = new ArrayList<>();
    }

    public List<Path> split(Params params, Path workDir) {
        this.workDir = workDir;
        var path = params.getSrcPath();
        var charset = params.getCharset();
        var k = Math.min(params.getK(), MAX_SPLIT_SIZE);

        // open input data stream and create sorted split files of k lines
        if (params.getFileExtensions().isPresent()) {
            openSplitStream(path, params.getFileExtensions().get(), charset, k, onSplitData);
        } else {
            openSplitStream(path, charset, k, onSplitData);
        }

        logger.log(Level.INFO, "All split files saved to {0}", new Object[] { workDir.toString() });
        return splitFiles;
    }

    private BiConsumer<String[], Integer> onSplitData = (data, splitId) -> {
        // sort input chunk data
        sorter.sort(data);
        logger.log(Level.FINE, "Split {0}: {1} lines sorting done.", new Object[] { splitId, data.length });

        // save this chunk to a file
        Path path = getFilePath(workDir, splitId);
        splitFiles.add(path);
        createSplitFile(path, data, splitId);
    };

    private void openSplitStream(Path path, String[] fileExtensions, Charset charset, int splitSize,
            BiConsumer<String[], Integer> onStreamData) {
        logger.log(Level.INFO, "Open input split stream for directory {0}", new Object[] { path.toString() });

        try (Stream<String> stream = fileReader.lines(path, fileExtensions, charset)) {
            streamUtils.processInBatch(stream, splitSize, onStreamData);
        } catch (IOException e) {
            throw new BigSortException(String.format("Error occured in split stream directory %s", path.toString()), e);
        }
    }

    private void openSplitStream(Path path, Charset charset, int splitSize,
            BiConsumer<String[], Integer> onStreamData) {
        logger.log(Level.INFO, "Open input split stream file {0}", new Object[] { path.toString() });

        try (Stream<String> stream = fileReader.lines(path, charset)) {
            streamUtils.processInBatch(stream, splitSize, onStreamData);
        } catch (IOException e) {
            throw new BigSortException(String.format("Error occured in split stream file %s", path.toString()), e);
        }
    }

    private void createSplitFile(Path path, String[] data, int splitId) {
        try {
            fileWriter.write(path, data, StandardOpenOption.CREATE);
            path.toFile().deleteOnExit();
            logger.log(Level.FINE, "Split {0}: file created at {1}", new Object[] { splitId, path.toString() });
        } catch (IOException e) {
            throw new BigSortException(String.format("Error occured while creating split file {0}", path.toString()),
                    e);
        }
    }

    private Path getFilePath(Path dir, int splitId) {
        String fileName = String.format("%d%s", splitId, splitFileExtension);
        return dir.resolve(fileName);
    }
}
package bigsort.util.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * SortedFileReader
 */
public interface SortedFileReader extends AutoCloseable {
    /**
     * Initializes by creating LineReaders for provided files.
     * 
     * @param files
     * @param batchSize
     * @throws IOException
     */
    void initialize(List<Path> files, int batchSize) throws IOException;

    /**
     * Returns next most ascending line among all current lines pointing by
     * LineReaders.
     * 
     * @return Optional<String>
     * @throws IOException
     */
    Optional<String> readNextSortedLine() throws IOException;

    /**
     * Read all the lines of files in ascending order.
     * 
     * @return Stream<String>
     */
    Stream<String> readAllSortedLines();

    @Override
    void close();
}
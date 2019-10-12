package bigsort.util.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * LineReader will help to read a file sequentially line by line.
 */
public interface LineReader extends AutoCloseable {
    /**
     * Creates and returns the newly created LineReader with given path
     * 
     * @param path
     * @return LineReader
     * @throws IOException
     */
    LineReader create(Path path) throws IOException;

    /**
     * Get current line but don't read next line. If this is called first time, it
     * will read the first line.
     * 
     * @return Optional<String>
     * @throws IOException
     */
    Optional<String> line() throws IOException;

    /**
     * Read next line and store that so that calling line() can return later.
     * 
     * @return Optional<String>
     * @throws IOException
     */
    Optional<String> next() throws IOException;

    @Override
    void close();
}
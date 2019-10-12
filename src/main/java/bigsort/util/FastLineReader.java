package bigsort.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import bigsort.util.api.LineReader;

/**
 * FastLineReader is a wrapper of BufferedReader
 */
public class FastLineReader implements LineReader {
    private BufferedReader reader = null;
    private Optional<String> line = null;

    public FastLineReader() {

    }

    public FastLineReader(Path path) throws IOException {
        this.reader = Files.newBufferedReader(path);
    }

    @Override
    public LineReader create(Path path) throws IOException {
        return new FastLineReader(path);
    }

    @Override
    public synchronized Optional<String> line() throws IOException {
        return line != null ? line : next();
    }

    @Override
    public Optional<String> next() throws IOException {
        line = Optional.ofNullable(reader.readLine());
        return line;
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {

        }
    }
}
package bigsort.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * FastLineReaderTest
 */
public class FastLineReaderTest {

    @Test
    public void readsFirstLine(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("text.txt");

        Files.write(path, List.of("apple", "mango", "banana"));
        try (var reader = new FastLineReader(path)) {
            assertEquals("apple", reader.line().get());
        }
    }

    @Test
    public void readsFirstLineTwice(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("text.txt");

        Files.write(path, List.of("apple", "mango", "banana"));
        try (var reader = new FastLineReader(path)) {
            assertEquals("apple", reader.line().get());
            assertEquals("apple", reader.line().get());
        }
    }

    @Test
    public void readsFirstAndSecondLine(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("text.txt");

        Files.write(path, List.of("apple", "mango", "banana"));
        try (var reader = new FastLineReader(path)) {
            assertEquals("apple", reader.next().get());
            assertEquals("apple", reader.line().get());
            assertEquals("mango", reader.next().get());
        }
    }

    @Test
    public void readsEmpty(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("text.txt");

        Files.write(path, List.of("apple", "mango", "banana"));
        try (var reader = new FastLineReader(path)) {
            reader.next();
            reader.next();
            reader.next();
            assertTrue(reader.line().isPresent());
            assertTrue(reader.next().isEmpty());
        }
    }
}
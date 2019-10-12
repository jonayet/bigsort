package bigsort.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bigsort.TestUtil;
import bigsort.util.api.FileWriter;

/**
 * FastFileWritterTest
 */
public class FastFileWritterTest {

    @Test
    public void writesToFile(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("text.txt");
        FileWriter writer = new FastFileWriter();

        // write file
        writer.write(path, TestUtil.words());
        assertEquals(TestUtil.words(), Files.lines(path).collect(Collectors.toList()));
    }

    @Test
    public void writesStringArray(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("text.txt");
        FileWriter writer = new FastFileWriter();

        // write file
        writer.write(path, TestUtil.words().toArray(new String[0]));
        assertEquals(TestUtil.words(), Files.lines(path).collect(Collectors.toList()));
    }

    @Test
    public void createsTempDir(@TempDir Path tempDir) throws IOException {
        FileWriter writer = new FastFileWriter();
        Path path = writer.createTempDirectory(tempDir, "test-dir");
        assertTrue(Files.isDirectory(path));
    }
}
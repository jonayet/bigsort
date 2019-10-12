package bigsort.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bigsort.TestUtil;
import bigsort.util.api.FileReader;

/**
 * FastFileReaderTest
 */
public class FastFileReaderTest {

    @Test
    public void readsFromFile(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("text.txt");
        FileReader reader = new FastFileReader();

        // create file
        Files.write(path, TestUtil.words());

        try (Stream<String> stream = reader.lines(path)) {
            assertEquals(TestUtil.words(), stream.collect(Collectors.toList()));
        }
    }

    @Test
    public void readsFromDirectory(@TempDir Path tempDir) throws IOException {
        Path path1 = tempDir.resolve("text1.txt");
        Path path2 = tempDir.resolve("text2.txt");
        FileReader reader = new FastFileReader();

        // write files to tempDir
        List<String> words1 = TestUtil.randomWords(8).collect(Collectors.toList());
        List<String> words2 = TestUtil.randomWords(6).collect(Collectors.toList());
        Files.write(path1, words1);
        Files.write(path2, words2);

        try (Stream<String> stream = reader.lines(tempDir, new String[] { ".txt" })) {
            List<String> words = new ArrayList<>();
            words.addAll(words1);
            words.addAll(words2);
            assertEquals(words, stream.collect(Collectors.toList()));
        }
    }

    @Test
    @Disabled("Disabled because it takes long time to finish")
    public void measureReadPerformance(@TempDir Path tempDir) throws IOException {
        final long NO_OF_FILES = 30;
        final long NO_OF_LINES = 400000;
        final int BATCH_SIZE = 29;

        // write files
        for (int i = 0; i < NO_OF_FILES; i++) {
            Path path = tempDir.resolve(String.format("text-%d.txt", i));
            List<String> words = TestUtil.randomWords(NO_OF_LINES).collect(Collectors.toList());
            Files.write(path, words, StandardOpenOption.CREATE);
        }

        FileReader reader = new FastFileReader();
        TestUtil.startMeasurement();
        try (Stream<String> lineStream = reader.lines(tempDir, new String[] { ".txt" })) {
            StreamUtils utils = new StreamUtils();
            AtomicInteger count = new AtomicInteger(0);
            utils.processInBatch(lineStream, BATCH_SIZE, (buffer, batchNo) -> {
                count.addAndGet(buffer.length);
            });
            assertEquals(NO_OF_FILES * NO_OF_LINES, count.get());
        }
        TestUtil.printMeasurement();
    }
}
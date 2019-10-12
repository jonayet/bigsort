package bigsort.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import bigsort.TestUtil;
import bigsort.fake.FakeLineReader;

/**
 * FastSortedFileReaderTest
 */
public class FastSortedFileReaderTest {
    List<Path> files = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        files.clear();
        for (int i = 0; i < 3; i++) {
            files.add(Path.of(String.format("file-%d.txt", i)));
        }

        TestUtil.clearFileSystem();
        TestUtil.createFile(files.get(0), Arrays.asList("appple", "avocado", "banana"));
        TestUtil.createFile(files.get(1), Arrays.asList("grape", "mango"));
        TestUtil.createFile(files.get(2), Arrays.asList("ball", "bread", "coffee", "milk"));
    }

    @Test
    public void getsAllWordsInSortedOrder() throws IOException {
        int BATCH_SIZE = 2;

        try (var reader = new FastSortedFileReader(new FakeLineReader())) {
            reader.initialize(files, BATCH_SIZE);

            List<String> words = new ArrayList<>();
            while (true) {
                Optional<String> value = reader.readNextSortedLine();
                value.ifPresent(words::add);
                if (value.isEmpty()) {
                    break;
                }
            }

            assertEquals(
                    Arrays.asList("appple", "avocado", "ball", "banana", "bread", "coffee", "grape", "mango", "milk"),
                    words);
        }
    }

    @Test
    public void getsAllWordsFasterWithSameBatchSize() throws IOException {
        int BATCH_SIZE = files.size();
        try (var reader = new FastSortedFileReader(new FakeLineReader())) {
            reader.initialize(files, BATCH_SIZE);

            List<String> words = new ArrayList<>();
            while (true) {
                Optional<String> value = reader.readNextSortedLine();
                value.ifPresent(words::add);
                if (value.isEmpty()) {
                    break;
                }
            }

            assertEquals(
                    Arrays.asList("appple", "avocado", "ball", "banana", "bread", "coffee", "grape", "mango", "milk"),
                    words);
        }
    }

    @Test
    public void getsSortedWordsAsStream() throws IOException {
        int BATCH_SIZE = 3;
        try (var reader = new FastSortedFileReader(new FakeLineReader())) {
            reader.initialize(files, BATCH_SIZE);
            List<String> words = reader.readAllSortedLines().limit(6).collect(Collectors.toList());
            assertEquals(Arrays.asList("appple", "avocado", "ball", "banana", "bread", "coffee"), words);
        }
    }

    @Test
    @Disabled("Disabled because it takes long time to finish")
    public void loadTestReadEachLineFromEveryFiles() throws IOException {
        int NO_OF_FILES = 50;
        int BATCH_SIZE = 50;
        List<String> input = new ArrayList<>();

        Supplier<Integer> length = () -> {
            int[] choices = { 54051, 50067, 29021 };
            return choices[new Random().nextInt(choices.length)];
        };

        // create files
        files.clear();
        TestUtil.clearFileSystem();
        for (int i = 0; i < NO_OF_FILES; i++) {
            files.add(Path.of(String.format("file-%d.txt", i)));
            List<String> words = TestUtil.randomWords(length.get()).collect(Collectors.toList());

            // add to inputWords
            input.addAll(words);

            // write words in sorted worder
            Collections.sort(words);
            TestUtil.createFile(files.get(i), words);
        }

        try (var reader = new FastSortedFileReader(new FakeLineReader())) {
            reader.initialize(files, BATCH_SIZE);
            Collections.sort(input);
            TestUtil.startMeasurement();
            List<String> output = reader.readAllSortedLines().collect(Collectors.toList());
            TestUtil.printMeasurement();
            assertEquals(input, output);
        }
    }
}
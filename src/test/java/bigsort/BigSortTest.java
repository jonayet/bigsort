package bigsort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bigsort.fake.FakeFileReader;
import bigsort.fake.FakeFileWriter;
import bigsort.fake.FakeLineReader;
import bigsort.util.FastFileReader;
import bigsort.util.FastFileWriter;
import bigsort.util.FastLineReader;
import bigsort.util.FastSortedFileReader;
import bigsort.util.FastSorter;
import bigsort.util.StreamUtils;
import bigsort.util.api.FileReader;
import bigsort.util.api.FileWriter;
import bigsort.util.api.SortedFileReader;
import bigsort.util.api.Sorter;

public class BigSortTest {
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tH:%1$tM:%1$tS.%1$tL [%4$-7s] (%2$s) %5$s %6$s%n");
        Arrays.stream(LogManager.getLogManager().getLogger("").getHandlers()).forEach(h -> h.setLevel(Level.OFF));
    }

    @BeforeEach
    public void beforeEach() {
        TestUtil.clearFileSystem();
    }

    @Test
    public void sortsFilesWithSmallK() throws IOException {
        Path srcPath = Path.of("fake", "test");
        Path destPath = Path.of("fake", "out.txt");
        int NO_OF_Files = 2;

        // create fake files
        FileWriter writer = new FakeFileWriter();
        List<String> inputWords = new ArrayList<>();
        for (int i = 0; i < NO_OF_Files; i++) {
            Path path = srcPath.resolve(String.format("test-%d.txt", i));
            List<String> words = TestUtil.randomWords(3, 4, 5).collect(Collectors.toList());
            inputWords.addAll(words);
            writer.write(path, words.stream()::iterator);
        }

        // build params
        int k = 200;
        String[] extensions = { ".txt" };
        Params params = new Params(srcPath, Optional.of(extensions), destPath, k, srcPath);

        // make BigSort instance
        FileReader reader = new FakeFileReader();
        SortedFileReader sortedReader = new FastSortedFileReader(new FakeLineReader());
        Sorter sorter = new FastSorter();
        StreamUtils streamUtils = new StreamUtils();
        Splitter spliter = new Splitter(reader, writer, sorter, streamUtils);
        Merger merger = new Merger(sortedReader, writer, streamUtils);
        BigSort bigSort = new BigSort(params, writer, spliter, merger);

        // sort and get sorted words
        bigSort.sort();
        List<String> actual = TestUtil.getFile(destPath);

        // sort input words and assert
        Collections.sort(inputWords);
        assertEquals(inputWords, actual);
    }

    @Test
    public void sortsFilesFasterWithGreaterK() throws IOException {
        Path srcPath = Path.of("fake", "test");
        Path destPath = Path.of("fake", "out.txt");
        int NO_OF_Files = 50;

        // create fake files
        FileWriter writer = new FakeFileWriter();
        List<String> inputWords = new ArrayList<>();
        for (int i = 0; i < NO_OF_Files; i++) {
            Path path = srcPath.resolve(String.format("test-%d.txt", i));
            List<String> words = TestUtil.randomWords(234, 282, 193).collect(Collectors.toList());
            inputWords.addAll(words);
            writer.write(path, words.stream()::iterator);
        }

        // build params
        int k = 200;
        String[] extensions = { ".txt" };
        Params params = new Params(srcPath, Optional.of(extensions), destPath, k, srcPath);

        // make BigSort instance
        FileReader reader = new FakeFileReader();
        SortedFileReader sortedReader = new FastSortedFileReader(new FakeLineReader());
        Sorter sorter = new FastSorter();
        StreamUtils streamUtils = new StreamUtils();
        Splitter spliter = new Splitter(reader, writer, sorter, streamUtils);
        Merger merger = new Merger(sortedReader, writer, streamUtils);
        BigSort bigSort = new BigSort(params, writer, spliter, merger);

        // sort and get sorted words
        bigSort.sort();
        List<String> actual = TestUtil.getFile(destPath);

        // sort input words and assert
        Collections.sort(inputWords);
        assertEquals(inputWords, actual);
    }

    @Test
    public void sortsRealFiles(@TempDir Path tempDir) throws IOException {
        Path srcPath = Path.of(tempDir.toString(), "src");
        Path destPath = Path.of(tempDir.toString(), "out.txt");
        int NO_OF_Files = 5;

        // create files
        Files.createDirectories(srcPath);
        FileWriter writer = new FastFileWriter();
        for (int i = 0; i < NO_OF_Files; i++) {
            Path path = srcPath.resolve(String.format("test-%d.txt", i));
            writer.write(path, TestUtil.randomWords(20, 34, 31)::iterator);
        }

        // build params
        int k = 200;
        String[] extensions = { ".txt" };
        Params params = new Params(srcPath, Optional.of(extensions), destPath, k, srcPath);

        // make BigSort instance
        FileReader reader = new FastFileReader();
        SortedFileReader sortedReader = new FastSortedFileReader(new FastLineReader());
        Sorter sorter = new FastSorter();
        StreamUtils streamUtils = new StreamUtils();
        Splitter spliter = new Splitter(reader, writer, sorter, streamUtils);
        Merger merger = new Merger(sortedReader, writer, streamUtils);
        BigSort bigSort = new BigSort(params, writer, spliter, merger);

        // sort and get sorted words
        bigSort.sort();
        List<String> actual = new ArrayList<>();
        reader.lines(destPath).forEach(actual::add);

        // get expected list and assert
        List<String> expected = new ArrayList<>();
        reader.lines(srcPath, extensions).forEach(expected::add);
        Collections.sort(expected);
        assertEquals(expected, actual);
    }

    @Test
    @Disabled("Test it individually")
    public void testsPerformance() throws IOException {
        Path srcPath = Path.of("fake", "test");
        Path destPath = Path.of("fake", "out.txt");
        int NO_OF_Files = 500;

        // create fake files
        FileWriter writer = new FakeFileWriter();
        List<String> inputWords = new ArrayList<>();
        for (int i = 0; i < NO_OF_Files; i++) {
            Path path = srcPath.resolve(String.format("test-%d.txt", i));
            List<String> words = TestUtil.randomWords(2634, 2582, 1593).collect(Collectors.toList());
            inputWords.addAll(words);
            writer.write(path, words.stream()::iterator);
        }

        // build params
        int k = 500000;
        String[] extensions = { ".txt" };
        Params params = new Params(srcPath, Optional.of(extensions), destPath, k, srcPath);

        // make BigSort instance
        FileReader reader = new FakeFileReader();
        SortedFileReader sortedReader = new FastSortedFileReader(new FakeLineReader());
        Sorter sorter = new FastSorter();
        StreamUtils streamUtils = new StreamUtils();
        Splitter spliter = new Splitter(reader, writer, sorter, streamUtils);
        Merger merger = new Merger(sortedReader, writer, streamUtils);
        BigSort bigSort = new BigSort(params, writer, spliter, merger);

        // sort and get sorted words
        TestUtil.startMeasurement();
        bigSort.sort();
        TestUtil.printMeasurement();
        List<String> actual = TestUtil.getFile(destPath);

        // sort input words and assert
        Collections.sort(inputWords);
        assertEquals(inputWords, actual);
    }

    @Test
    @Disabled("Test it individually")
    public void testPerformanceWithRealFiles(@TempDir Path tempDir) throws IOException {
        // Path tempDir = Path.of(System.getProperty("user.home"), "Downloads");
        Path srcPath = Path.of(tempDir.toString(), "test", "src");
        Path destPath = Path.of(tempDir.toString(), "test", "out.txt");
        FileWriter writer = new FastFileWriter();

        // create files
        Files.createDirectories(srcPath);
        int NO_OF_Files = 5;
        for (int i = 0; i < NO_OF_Files; i++) {
            Path path = srcPath.resolve(String.format("test-%d.txt", i));
            writer.write(path, TestUtil.randomWords(866850, 884633, 853550)::iterator);
        }

        // build params
        int k = 999999;
        String[] extensions = { ".txt" };
        Params params = new Params(srcPath, Optional.of(extensions), destPath, k, srcPath);

        // make BigSort instance
        FileReader reader = new FastFileReader();
        SortedFileReader sortedReader = new FastSortedFileReader(new FastLineReader());
        Sorter sorter = new FastSorter();
        StreamUtils streamUtils = new StreamUtils();
        Splitter spliter = new Splitter(reader, writer, sorter, streamUtils);
        Merger merger = new Merger(sortedReader, writer, streamUtils);
        BigSort bigSort = new BigSort(params, writer, spliter, merger);

        // sort and assert
        TestUtil.startMeasurement();
        bigSort.sort();
        TestUtil.printMeasurement();
        assertTrue(reader.lines(destPath).count() > 0);
    }
}

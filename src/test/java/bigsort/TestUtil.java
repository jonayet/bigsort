package bigsort;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import bigsort.util.Measurement;

/**
 * TestUtil
 */
public class TestUtil {
    private static Measurement measurement = new Measurement();
    private static ConcurrentHashMap<Path, List<String>> fakeFileSyetem = new ConcurrentHashMap<>();

    public static void startMeasurement() {
        measurement.startMeasurement();
        System.out.println("------------------------------------");
        System.out.println("Measuring resource consumption...");
        System.out.println("Process ID: " + measurement.getProcessId());
        System.out.println("------------------------------------");
    }

    public static void printMeasurement() {
        measurement.finishMeasurement();
        System.out.println("------------------------------------");
        System.out.println("Elapsed Time: " + measurement.getElapsedTime());
        System.out.println("Used Memory: " + measurement.getMemoryUsage());
        System.out.println("------------------------------------");
    }

    public static List<String> words() {
        return List.of("apple", "apricot", "avocado", "banana", "berry", "cantaloupe", "cherry", "citron", "citrus",
                "coconut", "date", "fig", "grape", "guava", "kiwi", "lemon", "lime", "mango", "melon", "mulberry",
                "nectarine", "orange", "papaya", "peach", "pear", "pineapple", "plum", "prune", "raisin", "raspberry",
                "tangerine");
    }

    public static Stream<String> randomWords(long... length) {
        return Stream.iterate(1L, i -> i + 1).map(i -> rand(words()) + "-" + i).limit(rand(length));
    }

    public static void clearFileSystem() {
        fakeFileSyetem.clear();
    }

    public static ConcurrentHashMap<Path, List<String>> getFileSystem() {
        return fakeFileSyetem;
    }

    public static void createFile(Path path, List<String> content) {
        fakeFileSyetem.put(path, content);
    }

    public static List<String> getFile(Path path) {
        return fakeFileSyetem.get(path);
    }

    public static Optional<String> readLine(Path path, long lineNo) throws FileNotFoundException {
        List<String> file = getFile(path);
        if (file == null) {
            throw new FileNotFoundException(path.toString());
        }

        if (lineNo >= file.size()) {
            return Optional.empty();
        }

        return Optional.of(file.get((int) lineNo));
    }

    private static long rand(long[] choices) {
        int index = new Random().nextInt(choices.length);
        return choices[index];
    }

    private static String rand(List<String> choices) {
        int index = new Random().nextInt(choices.size());
        return choices.get(index);
    }
}
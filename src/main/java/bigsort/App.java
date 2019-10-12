package bigsort;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import bigsort.util.FastFileReader;
import bigsort.util.FastFileWriter;
import bigsort.util.FastLineReader;
import bigsort.util.FastSortedFileReader;
import bigsort.util.FastSorter;
import bigsort.util.StreamUtils;
import bigsort.util.api.FileReader;
import bigsort.util.api.FileWriter;
import bigsort.util.api.LineReader;
import bigsort.util.api.SortedFileReader;
import bigsort.util.api.Sorter;

public final class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        Params params;
        try {
            configureLogger();
            params = new Params(args);
            printParams(params);
            var bigSort = buildBigSort(params);
            bigSort.sort();
        } catch (InvalidParameterException e) {
            System.out.println("");
            System.out.println(e.getMessage());
            System.out.println("");
            System.out.println("Please provide right arguments like following,");
            System.out.println("srcPath destPath k [ .txt,.log ]");
            System.out.println("");
        } catch (Exception e) {
            System.out.println("Unknown error occured.");
            logger.log(Level.SEVERE, e.toString());
        }
    }

    private static BigSort buildBigSort(Params params) {
        FileWriter fileWriter = new FastFileWriter();
        FileReader fileReader = new FastFileReader();
        LineReader lineReader = new FastLineReader();
        Sorter sorter = new FastSorter();
        StreamUtils streamUtils = new StreamUtils();
        SortedFileReader sortedFileReader = new FastSortedFileReader(lineReader);
        Splitter spliter = new Splitter(fileReader, fileWriter, sorter, streamUtils);
        Merger merger = new Merger(sortedFileReader, fileWriter, streamUtils);
        return new BigSort(params, fileWriter, spliter, merger);
    }

    private static void configureLogger() {
        try (var file = new FileInputStream("./logger.properties")) {
            LogManager.getLogManager().readConfiguration(file);
            return;
        } catch (SecurityException | IOException | NullPointerException e) {
        }

        System.out.println("WARNING: logger.properties file not found");
        System.out.println("WARNING: configuring logger with defaults");
        Arrays.stream(LogManager.getLogManager().getLogger("").getHandlers()).forEach(h -> h.setLevel(Level.INFO));
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT %1$tL] [%4$-7s] %5$s %n");
    }

    private static void printParams(Params params) {
        logger.info("================================= Params ============================================");
        logger.info("srcPath: " + params.getSrcPath().toString());
        if (params.getFileExtensions().isPresent()) {
            logger.info("fileExtensions: " + Arrays.toString(params.getFileExtensions().get()));
        }
        logger.info("destPath: " + params.getDestPath().toString());
        logger.info(String.format("k: %s (using: %d)", params.getKOriginal(), params.getK()));
        logger.info("tempDir: " + params.getTempDir().toString());
        logger.info("======================================================================================");
    }
}

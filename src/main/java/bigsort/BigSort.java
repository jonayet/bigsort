package bigsort;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import bigsort.exceptions.BigSortException;
import bigsort.util.Measurement;
import bigsort.util.api.FileWriter;

/**
 * BigSort will use Merger and Splitter to read the input files and will sort
 * this large number of data using external merge technique.
 */
public class BigSort {
    private final Params params;
    private final FileWriter fileWriter;
    private final Splitter spliter;
    private final Merger merger;
    private final Measurement measurement;
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public BigSort(Params params, FileWriter fileWriter, Splitter spliter, Merger merger) {
        this.params = params;
        this.fileWriter = fileWriter;
        this.spliter = spliter;
        this.merger = merger;
        this.measurement = new Measurement();
    }

    public void sort() {
        try {
            measurement.startMeasurement();
            logger.info("======================================================================================");
            logger.info("Process ID: " + measurement.getProcessId());
            logger.info("======================================================================================");

            // create temp work directory
            Path workDir = createWorkDir(params.getTempDir(), params.getWorkDirPrefix());
            logger.info("workDir: " + workDir.toString());

            List<Path> splitFiles = spliter.split(params, workDir);
            logger.log(Level.INFO, "{0} splited files created", new Object[] { splitFiles.size() });
            logger.info("======================================================================================");

            // read every split files and append lines in sorted order to destPath file
            merger.merge(splitFiles, params);
            logger.log(Level.INFO, "Completed. Sorted file saved to {0}",
                    new Object[] { params.getDestPath().toString() });
            measurement.finishMeasurement();
            logger.info("======================================================================================");
            logger.info("Required Time: " + measurement.getElapsedTime());
            logger.info("Used Memory: " + measurement.getMemoryUsage());
            logger.info("======================================================================================");

        } catch (BigSortException e) {
            logger.log(Level.SEVERE, "Error occured while sorting");
            logger.log(Level.SEVERE, e.toString());
        }
    }

    private Path createWorkDir(Path tempDir, String prefix) {
        try {
            Path dir = fileWriter.createTempDirectory(tempDir, prefix);
            dir.toFile().deleteOnExit();
            return dir;
        } catch (IOException e) {
            throw new BigSortException("Error occured while creating work directory", e);
        }
    }
}
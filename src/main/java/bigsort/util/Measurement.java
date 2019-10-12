package bigsort.util;

import java.lang.management.ManagementFactory;
import java.util.Locale;

/**
 * Measurement
 */
public class Measurement {
    private Runtime runtime;
    private String processId;
    private long startTime = 0;
    private long startMemory = 0;
    private long usedMemory = 0;
    private long elapsedTime = 0;

    public Measurement() {
        runtime = Runtime.getRuntime();
    }

    public void startMeasurement() {
        runtime.gc();
        processId = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        startMemory = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.currentTimeMillis();
    }

    public void finishMeasurement() {
        long finishMemory = runtime.totalMemory() - runtime.freeMemory();
        long endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;
        usedMemory = (finishMemory - startMemory) / (1024L * 1024L);
    }

    public String getProcessId() {
        return processId;
    }

    public String getElapsedTime() {
        return String.format(Locale.US, "%,d ms", elapsedTime);
    }

    public String getMemoryUsage() {
        return String.format(Locale.US, "%,d MB", usedMemory);
    }
}
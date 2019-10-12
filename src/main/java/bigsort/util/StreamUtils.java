package bigsort.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * StreamUtils is used to buffer a string stream data and to provide a callback
 * to process when the buffer is full or stream has ended.
 */
public class StreamUtils {
    public void processInBatch(Stream<String> stream, int batchSize, BiConsumer<String[], Integer> consumer) {
        final String[] buffer = new String[batchSize];
        final AtomicInteger batchId = new AtomicInteger(0);
        final AtomicInteger bufferIndex = new AtomicInteger(0);

        stream.forEach(element -> {
            synchronized (buffer) {
                if (bufferIndex.get() < batchSize) {
                    buffer[bufferIndex.getAndIncrement()] = element;
                    return;
                }

                consumer.accept(buffer, batchId.getAndIncrement());
                bufferIndex.set(0);
                buffer[bufferIndex.getAndIncrement()] = element;
            }
        });

        if (bufferIndex.get() > 0) {
            synchronized (buffer) {
                String[] smallerBuffer = new String[bufferIndex.get()];
                System.arraycopy(buffer, 0, smallerBuffer, 0, bufferIndex.get());
                consumer.accept(smallerBuffer, batchId.getAndIncrement());
            }
        }
    }
}
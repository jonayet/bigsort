package bigsort.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * StreamUtilsTest
 */
public class StreamUtilsTest {

    @Test
    public void processesInBatch() {
        int BATCH_SIZE = 201;
        int NO_OF_ITEMS = 10000;
        StreamUtils streamUtils = new StreamUtils();

        Stream<String> stream = Stream.iterate(0, i -> i + 1).map(i -> Integer.toString(i)).limit(NO_OF_ITEMS);

        streamUtils.processInBatch(stream, BATCH_SIZE, (numbers, batchNo) -> {
            int start = batchNo * BATCH_SIZE;
            int end = Math.min(start + BATCH_SIZE, NO_OF_ITEMS);
            Stream<String> expected = IntStream.range(start, end).mapToObj(Integer::toString);
            assertEquals(expected.collect(Collectors.toList()), Arrays.asList(numbers));
            expected.close();
        });
    }
}
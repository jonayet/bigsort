package bigsort.util;

import java.util.Optional;

/**
 * This class is used by FastSortedFileReader to track which line has been read
 * out form the Priority Queue.
 */
class Line {
    private int fileNo;
    private Optional<String> value;

    public Line(int fileNo, Optional<String> value) {
        this.fileNo = fileNo;
        this.value = value;
    }

    public int fileNo() {
        return this.fileNo;
    }

    public Optional<String> value() {
        return this.value;
    }
}
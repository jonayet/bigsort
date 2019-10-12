package bigsort.fake;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import bigsort.TestUtil;
import bigsort.util.api.LineReader;

/**
 * FakeLineReader
 */
public class FakeLineReader implements LineReader {
    private List<String> file;
    private Optional<String> line = null;
    private int index = 0;

    public FakeLineReader() {

    }

    public FakeLineReader(Path path) {
        this.file = TestUtil.getFile(path);
    }

    @Override
    public void close() {

    }

    @Override
    public LineReader create(Path path) {
        return new FakeLineReader(path);
    }

    @Override
    public Optional<String> line() {
        return line != null ? line : next();
    }

    @Override
    public Optional<String> next() {
        if (index == file.size()) {
            line = Optional.empty();
            return line;
        }

        line = Optional.of(file.get(index++));
        return line;
    }
}
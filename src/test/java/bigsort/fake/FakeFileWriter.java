package bigsort.fake;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import bigsort.TestUtil;
import bigsort.util.api.FileWriter;

/**
 * FakeFileWriter
 */
public class FakeFileWriter implements FileWriter {

    @Override
    public Path write(Path path, Iterable<String> lines, OpenOption... options) throws IOException {
        List<String> content = new ArrayList<>();
        if (Stream.of(options).anyMatch(o -> o == StandardOpenOption.APPEND)) {
            List<String> oldContent = TestUtil.getFile(path);
            if (oldContent != null) {
                content.addAll(oldContent);
            }
        }

        lines.forEach(content::add);
        TestUtil.createFile(path, content);
        return path;
    }

    @Override
    public Path write(Path path, String[] data, OpenOption... options) throws IOException {
        List<String> content = new ArrayList<>();
        if (Stream.of(options).anyMatch(o -> o == StandardOpenOption.APPEND)) {
            List<String> oldContent = TestUtil.getFile(path);
            if (oldContent != null) {
                content.addAll(oldContent);
            }
        }

        for (String line : data) {
            content.add(line);
        }

        TestUtil.createFile(path, content);
        return path;
    }

    @Override
    public Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs) throws IOException {
        Path dirPath = Path.of(dir.toString(), prefix);
        TestUtil.createFile(dirPath, List.of());
        return dirPath;
    }
}
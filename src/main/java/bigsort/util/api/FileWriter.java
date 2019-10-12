package bigsort.util.api;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

/**
 * FileWriter is a abstract layer of java.nio.file.Files
 */
public interface FileWriter {
    Path write(Path path, String[] data, OpenOption... options) throws IOException;

    Path write(Path path, Iterable<String> data, OpenOption... options) throws IOException;

    Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs) throws IOException;
}
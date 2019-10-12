package bigsort.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import bigsort.util.api.FileWriter;

/**
 * FastFileWriter is a wrapper of java.nio.file.Files
 */
public class FastFileWriter implements FileWriter {
    @Override
    public Path write(Path path, String[] data, OpenOption... options) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, options)) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
        }
        return path;
    }

    @Override
    public Path write(Path path, Iterable<String> data, OpenOption... options) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, options)) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
        }
        return path;
    }

    @Override
    public Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs) throws IOException {
        return Files.createTempDirectory(dir, prefix, attrs);
    }
}
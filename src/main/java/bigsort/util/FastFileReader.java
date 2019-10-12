package bigsort.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Stream;

import bigsort.util.api.FileReader;

/**
 * A wrapper of java.nio.file.Files
 */
public class FastFileReader implements FileReader {

    @Override
    public Stream<String> lines(Path filePath) throws IOException {
        return lines(filePath, StandardCharsets.UTF_8);
    }

    @Override
    public Stream<String> lines(Path filePath, Charset charset) throws IOException {
        if (Files.isRegularFile(filePath)) {
            new FileNotFoundException(String.format("Path: \"%s\" is not a valid file.", filePath.toString()));
        }

        return readLines(filePath, charset);
    }

    @Override
    public Stream<String> lines(Path dirPath, String[] fileExtensions) throws IOException {
        return lines(dirPath, fileExtensions, StandardCharsets.UTF_8);
    }

    @Override
    public Stream<String> lines(Path dirPath, String[] fileExtensions, Charset charset) throws IOException {
        return files(dirPath, fileExtensions).flatMap(path -> {
            try {
                return readLines(path, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Stream<Path> files(Path dirPath, String[] fileExtensions) throws IOException {
        if (!Files.isDirectory(dirPath)) {
            throw new FileNotFoundException(
                    String.format("Path: \"%s\" is not a valid directory.", dirPath.toString()));
        }

        if (fileExtensions == null) {
            throw new IllegalArgumentException("\"fileExtensions\" must need to be provided.");
        }

        Predicate<Path> hasExtentionMatch = path -> {
            String filePath = path.toString();
            for (String suffix : fileExtensions) {
                if (filePath.endsWith(suffix)) {
                    return true;
                }
            }
            return false;
        };

        return Files.list(dirPath).filter(Files::isRegularFile).filter(hasExtentionMatch);
    }

    private Stream<String> readLines(Path path, Charset charset) throws IOException {
        return Files.lines(path, charset);
    }
}
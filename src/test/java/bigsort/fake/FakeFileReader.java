package bigsort.fake;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.stream.Stream;

import bigsort.TestUtil;
import bigsort.util.api.FileReader;

/**
 * FakeFileReader
 */
public class FakeFileReader implements FileReader {
    @Override
    public Stream<String> lines(Path filePath) throws IOException {
        synchronized (this) {
            return TestUtil.getFile(filePath).stream();
        }
    }

    @Override
    public Stream<String> lines(Path filePath, Charset charset) throws IOException {
        return lines(filePath);
    }

    @Override
    public Stream<String> lines(Path dirPath, String[] fileExtensions) throws IOException {
        return TestUtil.getFileSystem().entrySet().stream().filter(entry -> {
            Path filePath = entry.getKey();
            Path targetPath = dirPath.resolve(filePath.getFileName());
            return filePath.compareTo(targetPath) == 0;
        }).flatMap(file -> file.getValue().stream());
    }

    @Override
    public Stream<String> lines(Path dirPath, String[] fileExtensions, Charset charset) throws IOException {
        return lines(dirPath, fileExtensions);
    }

    @Override
    public Stream<Path> files(Path dirPath, String[] fileExtensions) throws IOException {
        return TestUtil.getFileSystem().entrySet().stream()
                .filter(file -> file.getKey().toString().startsWith(dirPath.toString())).map(entry -> entry.getKey());
    }
}
package bigsort.util.api;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * FileReader is a abstract layer of reading file contents as stream of string
 * regardless of if the source is a file or diectory.
 */
public interface FileReader {
    /**
     * Reads all lines of the file specified by filePath. StandardCharsets.UTF_8
     * will be used to read the file.
     * 
     * @param filePath
     * @return
     * @throws IOException
     */
    Stream<String> lines(Path filePath) throws IOException;

    /**
     * Reads all lines of the file specified by filePath. Specified charset will be
     * used to read the file.
     * 
     * @param filePath
     * @param charset
     * @return
     * @throws IOException
     */
    Stream<String> lines(Path filePath, Charset charset) throws IOException;

    /**
     * Reads all files of all files from specified directory. Nested directories
     * will be ignored. StandardCharsets.UTF_8 will be used to read files.
     * 
     * @param dirPath
     * @param fileExtensions
     * @return
     * @throws IOException
     */
    Stream<String> lines(Path dirPath, String[] fileExtensions) throws IOException;

    /**
     * Reads all files of all files from specified directory. Nested directories
     * will be ignored. Specified charset will be used to read files.
     * 
     * @param dirPath
     * @param fileExtensions
     * @return
     * @throws IOException
     */
    Stream<String> lines(Path dirPath, String[] fileExtensions, Charset charset) throws IOException;

    /**
     * Makes a stream of all files in the given directory.
     * 
     * @param dirPath
     * @param fileExtensions
     * @return
     * @throws IOException
     */
    Stream<Path> files(Path dirPath, String[] fileExtensions) throws IOException;
}
package bigsort;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Optional;
import java.util.UUID;

/**
 * Params validates and parse CLI arguments. It provides srcPath, destPath, k,
 * fileExtensions, tempDir, charset and workDirPrefix across the solution.
 */
public class Params {
    /**
     * A valid file path or a directory path to read files. If a directory,
     * "fileExtensions" need to provide.
     */
    private Path srcPath;

    /**
     * Src file estensions i.e [".txt", ".log"]. Required if "srcPath" is a
     * directory.
     */
    private Optional<String[]> fileExtensions;

    /**
     * A valid non-esists file path, will be used to save the sorted file.
     */
    private Path destPath;

    /**
     * A valid number >= 2, that restrics max number of words will be proccessed at
     * any time. If provided value is greater that Integer.MAX_VALUE, value will be
     * trimmed to Integer.MAX_VALUE will. This has been done intentionally to keep
     * chunk data in String[] array.
     */
    private int k;

    /**
     * Optional dir path will be used to save split files. System specific temp dir
     * will be used if not provided.
     */
    private Path tempDir;

    /**
     * Default value is StandardCharsets.UTF_8 and will be used to read and write
     * files across the solution.
     */
    private Charset charset;

    /**
     * This will be used to create a temporary work directory to store the split
     * files.
     */
    private String workDirPrefix;

    private String kOriginal;
    private final BigInteger K_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
    private final String defTempDir = System.getProperty("java.io.tmpdir");

    public Params(String[] args) {
        validateParams(args);
        this.fileExtensions = getFileExtensions(args);
        this.srcPath = parseSrcPath(args[0], this.fileExtensions);
        this.destPath = parseDestPath(args[1]);
        this.k = parseK(args[2]).min(K_MAX).intValue();
        this.kOriginal = args[2];
        this.tempDir = parseTempDir(System.getProperty("tempDir", defTempDir));
        this.charset = StandardCharsets.UTF_8;
        this.workDirPrefix = UUID.randomUUID().toString();
    }

    public Params(Path src, Optional<String[]> fileExtensions, Path dest, int k) {
        this.srcPath = src;
        this.destPath = dest;
        this.k = k;
        this.fileExtensions = fileExtensions;
        this.tempDir = Path.of(defTempDir);
        this.charset = StandardCharsets.UTF_8;
        this.workDirPrefix = UUID.randomUUID().toString();
    }

    public Params(Path src, Optional<String[]> fileExtensions, Path dest, int k, Path tempDir) {
        this(src, fileExtensions, dest, k);
        this.tempDir = tempDir;
    }

    public Path getSrcPath() {
        return srcPath;
    }

    public int getK() {
        return k;
    }

    public String getKOriginal() {
        return kOriginal;
    }

    public Optional<String[]> getFileExtensions() {
        return fileExtensions;
    }

    public Path getDestPath() {
        return destPath;
    }

    public Charset getCharset() {
        return charset;
    }

    public Path getTempDir() {
        return tempDir;
    }

    public String getWorkDirPrefix() {
        return workDirPrefix;
    }

    private void validateParams(String[] args) {
        if (args.length < 3) {
            throw new InvalidParameterException("Insufficient arguments");
        }
    }

    private Path parseSrcPath(String src, Optional<String[]> fileExtensions) {
        Path path = Path.of(src);

        if (Files.isDirectory(path) && fileExtensions.isEmpty()) {
            throw new InvalidParameterException("srcPath is a directory but file extensions are missing.");
        }

        if (!Files.isDirectory(path) && !Files.isRegularFile(path)) {
            throw new InvalidParameterException("srcPath is not found.");
        }

        return path;
    }

    private Optional<String[]> getFileExtensions(String[] args) {
        if (args.length < 4) {
            return Optional.empty();
        }
        String extensions = args[3];
        return Optional.of(extensions.split(","));
    }

    private Path parseDestPath(String dest) {
        Path path = Path.of(dest);

        if (Files.isDirectory(path)) {
            throw new InvalidParameterException("destPath is a directory but a file path is required.");
        }

        if (Files.isRegularFile(path)) {
            throw new InvalidParameterException("destPath: file is already exists.");
        }

        if (!Files.isDirectory(path.getParent())) {
            throw new InvalidParameterException("destPath parent path doesn't exists.");
        }

        return path;
    }

    private BigInteger parseK(String kValue) {
        BigInteger k;
        try {
            k = new BigInteger(kValue);
        } catch (NumberFormatException ex) {
            throw new InvalidParameterException("k is not a valid number.");
        }

        if (k.compareTo(new BigInteger("2")) < 0) {
            throw new InvalidParameterException("k must be >= 2.");
        }

        return k;
    }

    private Path parseTempDir(String temp) {
        Path path = Path.of(temp);

        if (!Files.isDirectory(path)) {
            throw new InvalidParameterException("tempDir is not a directory.");
        }

        return path;
    }
}
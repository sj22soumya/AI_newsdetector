package exception;

/**
 * Thrown when a file read or write operation fails.
 */
public class FileStorageException extends RuntimeException {

    private final String filePath;

    public FileStorageException(String message) {
        super(message);
        this.filePath = "";
    }

    public FileStorageException(String filePath, String message) {
        super(message);
        this.filePath = filePath;
    }

    public FileStorageException(String filePath, String message, Throwable cause) {
        super(message, cause);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}

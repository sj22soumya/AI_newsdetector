package exception;

/**
 * Thrown when a news article is invalid (empty, too short, or otherwise unusable).
 */
public class InvalidNewsException extends RuntimeException {

    private final String field; // Which field caused the problem

    public InvalidNewsException(String message) {
        super(message);
        this.field = "unknown";
    }

    public InvalidNewsException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}

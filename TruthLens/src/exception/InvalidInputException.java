package exception;

/**
 * Thrown when user provides invalid menu input or command-line parameters.
 */
public class InvalidInputException extends RuntimeException {

    private final String input; // The offending input value

    public InvalidInputException(String message) {
        super(message);
        this.input = "";
    }

    public InvalidInputException(String input, String message) {
        super(message);
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}

package util;

import exception.InvalidNewsException;
import exception.InvalidInputException;
import java.io.File;

/**
 * Validates all user inputs before they are processed by the application.
 * Throws appropriate custom exceptions on invalid input.
 */
public final class InputValidator {

    private static final int MIN_HEADLINE_LENGTH = 5;
    private static final int MIN_CONTENT_LENGTH  = 20;

    private InputValidator() { /* utility class */ }

    /**
     * Validate a news headline.
     * @throws InvalidNewsException if headline is null, empty, or too short.
     */
    public static void validateHeadline(String headline) {
        if (headline == null || headline.trim().isEmpty()) {
            throw new InvalidNewsException("headline", "Headline cannot be empty.");
        }
        if (headline.trim().length() < MIN_HEADLINE_LENGTH) {
            throw new InvalidNewsException("headline",
                "Headline is too short. Minimum " + MIN_HEADLINE_LENGTH + " characters required.");
        }
    }

    /**
     * Validate article content.
     * @throws InvalidNewsException if content is null, empty, or too short.
     */
    public static void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidNewsException("content", "Article content cannot be empty.");
        }
        if (content.trim().length() < MIN_CONTENT_LENGTH) {
            throw new InvalidNewsException("content",
                "Article content is too short. Please provide more text (minimum "
                + MIN_CONTENT_LENGTH + " characters) for meaningful analysis.");
        }
    }

    /**
     * Validate a file path for reading.
     * @throws InvalidInputException if path is null/empty or file does not exist.
     */
    public static void validateFilePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new InvalidInputException(path, "File path cannot be empty.");
        }
        File file = new File(path.trim());
        if (!file.exists()) {
            throw new InvalidInputException(path, "File not found: " + path);
        }
        if (!file.isFile()) {
            throw new InvalidInputException(path, "Path is not a file: " + path);
        }
        if (!file.canRead()) {
            throw new InvalidInputException(path, "File cannot be read (permission denied): " + path);
        }
    }

    /**
     * Validate a menu choice integer string.
     * @throws InvalidInputException if input is not a valid integer within [min, max].
     */
    public static int validateMenuChoice(String input, int min, int max) {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException(input, "Input cannot be empty. Please enter a number.");
        }
        try {
            int choice = Integer.parseInt(input.trim());
            if (choice < min || choice > max) {
                throw new InvalidInputException(input,
                    "Invalid choice. Please enter a number between " + min + " and " + max + ".");
            }
            return choice;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(input, "Invalid input: '" + input.trim()
                + "'. Please enter a valid number.");
        }
    }

    /**
     * Validate a search keyword.
     * @throws InvalidInputException if keyword is null or empty.
     */
    public static void validateSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new InvalidInputException(keyword, "Search keyword cannot be empty.");
        }
    }

    /**
     * Validate a record ID for history operations.
     * @throws InvalidInputException if id is null or empty.
     */
    public static void validateRecordId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidInputException(id, "Record ID cannot be empty.");
        }
    }
}

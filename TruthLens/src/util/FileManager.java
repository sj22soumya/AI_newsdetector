package util;

import exception.FileStorageException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all file read/write operations for the application.
 * Ensures the data directory is created when needed.
 */
public final class FileManager {

    private FileManager() { /* utility class */ }

    /**
     * Read all lines from a file.
     * Returns an empty list if the file does not exist (first run).
     * @throws FileStorageException on read errors other than missing file.
     */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return lines; // Normal — first run
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new FileStorageException(filePath, "Failed to read file: " + filePath, e);
        }
        return lines;
    }

    /**
     * Read the complete content of a file as a single string.
     * @throws FileStorageException on any I/O error.
     */
    public static String readFileContent(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileStorageException(filePath, "File not found: " + filePath);
        }
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileStorageException(filePath, "Failed to read file: " + filePath, e);
        }
    }

    /**
     * Write a list of lines to a file, overwriting any existing content.
     * Creates the parent directory if it does not exist.
     * @throws FileStorageException on any I/O error.
     */
    public static void writeLines(String filePath, List<String> lines) {
        ensureParentDirectory(filePath);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath, false), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileStorageException(filePath, "Failed to write file: " + filePath, e);
        }
    }

    /**
     * Append a single line to a file.
     * Creates the file and parent directory if they do not exist.
     * @throws FileStorageException on any I/O error.
     */
    public static void appendLine(String filePath, String line) {
        ensureParentDirectory(filePath);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath, true), StandardCharsets.UTF_8))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new FileStorageException(filePath, "Failed to append to file: " + filePath, e);
        }
    }

    /**
     * Delete a file if it exists.
     * @throws FileStorageException if deletion fails.
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && !file.delete()) {
            throw new FileStorageException(filePath, "Failed to delete file: " + filePath);
        }
    }

    /**
     * Ensure the parent directory of a file path exists, creating it if needed.
     */
    public static void ensureParentDirectory(String filePath) {
        File parent = new File(filePath).getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}

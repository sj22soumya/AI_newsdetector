package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a persisted analysis history record.
 * Can be serialised to / deserialised from a pipe-delimited string for file storage.
 */
public class AnalysisRecord {

    private static final String DELIMITER  = "|";
    private static final String DATE_FMT   = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FMT);

    private String        id;
    private LocalDateTime timestamp;
    private String        headline;
    private int           riskScore;
    private String        classification;

    // Default constructor
    public AnalysisRecord() {}

    // Parameterised constructor
    public AnalysisRecord(String id, LocalDateTime timestamp,
                          String headline, int riskScore, String classification) {
        this.id             = id;
        this.timestamp      = timestamp;
        this.headline       = headline;
        this.riskScore      = riskScore;
        this.classification = classification;
    }

    // --- Getters ---
    public String        getId()             { return id; }
    public LocalDateTime getTimestamp()      { return timestamp; }
    public String        getHeadline()       { return headline; }
    public int           getRiskScore()      { return riskScore; }
    public String        getClassification() { return classification; }

    // --- Setters ---
    public void setId(String id)                         { this.id = id; }
    public void setTimestamp(LocalDateTime timestamp)    { this.timestamp = timestamp; }
    public void setHeadline(String headline)             { this.headline = headline; }
    public void setRiskScore(int riskScore)              { this.riskScore = riskScore; }
    public void setClassification(String classification) { this.classification = classification; }

    /** Formatted timestamp string */
    public String getFormattedTimestamp() {
        return (timestamp != null) ? timestamp.format(FORMATTER) : "Unknown";
    }

    /**
     * Serialise this record to a single pipe-delimited line.
     * Pipes within the headline are replaced with a safe escape sequence.
     */
    public String toStorageString() {
        String safeHeadline = headline.replace("|", "{{PIPE}}");
        return id + DELIMITER
             + getFormattedTimestamp() + DELIMITER
             + safeHeadline + DELIMITER
             + riskScore + DELIMITER
             + classification;
    }

    /**
     * Deserialise a record from a pipe-delimited line.
     * Returns null if the line is malformed.
     */
    public static AnalysisRecord fromStorageString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split("\\|", 5);
        if (parts.length < 5) return null;
        try {
            String        id             = parts[0].trim();
            LocalDateTime timestamp      = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            String        headline       = parts[2].trim().replace("{{PIPE}}", "|");
            int           riskScore      = Integer.parseInt(parts[3].trim());
            String        classification = parts[4].trim();
            return new AnalysisRecord(id, timestamp, headline, riskScore, classification);
        } catch (Exception e) {
            // Corrupted record — caller should handle this
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Score: %d | %s | %s",
            id, getFormattedTimestamp(), riskScore, classification, headline);
    }
}

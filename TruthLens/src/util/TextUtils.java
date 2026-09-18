package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for common text-processing operations.
 * Static methods only — not intended to be instantiated.
 */
public final class TextUtils {

    // Regex patterns
    private static final Pattern URL_PATTERN =
        Pattern.compile("(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern SENTENCE_SPLIT =
        Pattern.compile("[.!?]+\\s*");

    private TextUtils() { /* utility class */ }

    /**
     * Split text into individual words (alphabetic tokens only, trimmed).
     */
    public static List<String> splitWords(String text) {
        List<String> words = new ArrayList<>();
        if (text == null || text.isEmpty()) return words;
        // Split on whitespace and non-word characters
        String[] tokens = text.split("[\\s\\p{Punct}]+");
        for (String token : tokens) {
            String t = token.trim();
            if (!t.isEmpty()) words.add(t);
        }
        return words;
    }

    /**
     * Split text into sentences using common terminators.
     */
    public static List<String> splitSentences(String text) {
        List<String> sentences = new ArrayList<>();
        if (text == null || text.isEmpty()) return sentences;
        String[] parts = SENTENCE_SPLIT.split(text);
        for (String part : parts) {
            String s = part.trim();
            if (!s.isEmpty()) sentences.add(s);
        }
        return sentences;
    }

    /**
     * Count how many words in the list are fully uppercase (length >= 2).
     */
    public static int countUppercaseWords(List<String> words) {
        int count = 0;
        for (String word : words) {
            if (word.length() >= 2 && word.equals(word.toUpperCase())
                    && word.matches("[A-Z]+")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count occurrences of a specific character in the text.
     */
    public static int countChar(String text, char target) {
        if (text == null) return 0;
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == target) count++;
        }
        return count;
    }

    /**
     * Count URLs present in the text.
     */
    public static int countUrls(String text) {
        if (text == null || text.isEmpty()) return 0;
        Matcher m = URL_PATTERN.matcher(text);
        int count = 0;
        while (m.find()) count++;
        return count;
    }

    /**
     * Extract all URLs from the text.
     */
    public static List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<>();
        if (text == null || text.isEmpty()) return urls;
        Matcher m = URL_PATTERN.matcher(text);
        while (m.find()) urls.add(m.group());
        return urls;
    }

    /**
     * Count how many times a keyword phrase appears in text (case-insensitive).
     */
    public static int countKeywordOccurrences(String text, String keyword) {
        if (text == null || keyword == null || text.isEmpty() || keyword.isEmpty()) return 0;
        String lowerText = text.toLowerCase();
        String lowerKey  = keyword.toLowerCase();
        int count = 0;
        int index = 0;
        while ((index = lowerText.indexOf(lowerKey, index)) != -1) {
            count++;
            index += lowerKey.length();
        }
        return count;
    }

    /**
     * Calculate ratio safely — avoids division by zero.
     */
    public static double safeRatio(int numerator, int denominator) {
        if (denominator == 0) return 0.0;
        return (double) numerator / denominator;
    }

    /**
     * Truncate a string to maxLen characters, appending "..." if truncated.
     */
    public static String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen) + "...";
    }
}

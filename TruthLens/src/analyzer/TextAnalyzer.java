package analyzer;

import model.NewsArticle;
import util.TextUtils;
import java.util.*;

/**
 * Performs low-level statistical text analysis on a NewsArticle.
 * Returns structured statistics as a Map rather than printing directly.
 *
 * Demonstrates: encapsulation, single responsibility, collections.
 */
public class TextAnalyzer {

    /**
     * Analyse the full text of the article and return a map of statistics.
     * Keys are descriptive string labels; values are numeric results stored as Numbers.
     */
    public Map<String, Number> analyze(NewsArticle article) {
        String fullText = article.getFullText();
        String content  = article.getContent();

        List<String> words     = TextUtils.splitWords(fullText);
        List<String> sentences = TextUtils.splitSentences(content);

        int charCount         = fullText.length();
        int wordCount         = words.size();
        int sentenceCount     = Math.max(sentences.size(), 1); // avoid div/0
        int uppercaseWords    = TextUtils.countUppercaseWords(words);
        int exclamationCount  = TextUtils.countChar(fullText, '!');
        int questionMarkCount = TextUtils.countChar(fullText, '?');
        int urlCount          = TextUtils.countUrls(fullText);
        double avgSentLen     = TextUtils.safeRatio(wordCount, sentenceCount);
        double upperRatio     = TextUtils.safeRatio(uppercaseWords, wordCount);
        double exclRatio      = TextUtils.safeRatio(exclamationCount, wordCount);
        double questRatio     = TextUtils.safeRatio(questionMarkCount, wordCount);

        Map<String, Number> stats = new LinkedHashMap<>();
        stats.put("charCount",         charCount);
        stats.put("wordCount",         wordCount);
        stats.put("sentenceCount",     sentenceCount);
        stats.put("avgSentenceLength", avgSentLen);
        stats.put("uppercaseWordCount",uppercaseWords);
        stats.put("uppercaseRatio",    upperRatio);
        stats.put("exclamationCount",  exclamationCount);
        stats.put("exclamationRatio",  exclRatio);
        stats.put("questionMarkCount", questionMarkCount);
        stats.put("questionMarkRatio", questRatio);
        stats.put("urlCount",          urlCount);
        return stats;
    }

    /**
     * Return a formatted summary of statistics for display purposes.
     */
    public String formatStats(Map<String, Number> stats) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  Characters       : %d%n",   stats.get("charCount").intValue()));
        sb.append(String.format("  Words            : %d%n",   stats.get("wordCount").intValue()));
        sb.append(String.format("  Sentences        : %d%n",   stats.get("sentenceCount").intValue()));
        sb.append(String.format("  Avg Sentence Len : %.1f words%n", stats.get("avgSentenceLength").doubleValue()));
        sb.append(String.format("  UPPERCASE Words  : %d (%.0f%%)%n",
            stats.get("uppercaseWordCount").intValue(),
            stats.get("uppercaseRatio").doubleValue() * 100));
        sb.append(String.format("  Exclamation Marks: %d%n",   stats.get("exclamationCount").intValue()));
        sb.append(String.format("  Question Marks   : %d%n",   stats.get("questionMarkCount").intValue()));
        sb.append(String.format("  URLs Detected    : %d%n",   stats.get("urlCount").intValue()));
        return sb.toString();
    }
}

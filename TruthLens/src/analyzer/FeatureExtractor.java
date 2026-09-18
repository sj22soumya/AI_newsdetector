package analyzer;

import model.NewsArticle;
import model.NewsFeatures;
import util.TextUtils;
import java.util.*;

/**
 * Combines TextAnalyzer and KeywordAnalyzer outputs into a structured NewsFeatures object.
 * Acts as the bridge between raw analysis and the classifier.
 *
 * Demonstrates: composition, delegation, single responsibility.
 */
public class FeatureExtractor {

    private final TextAnalyzer    textAnalyzer;
    private final KeywordAnalyzer keywordAnalyzer;

    public FeatureExtractor() {
        this.textAnalyzer    = new TextAnalyzer();
        this.keywordAnalyzer = new KeywordAnalyzer();
    }

    /**
     * Extract all measurable features from a NewsArticle.
     *
     * Pipeline:
     *   NewsArticle → TextAnalyzer → KeywordAnalyzer → NewsFeatures
     */
    public NewsFeatures extract(NewsArticle article) {
        // Step 1: Text statistics
        Map<String, Number> stats = textAnalyzer.analyze(article);

        // Step 2: Keyword analysis
        Map<String, List<String>> kwResults = keywordAnalyzer.analyze(article.getFullText());

        // Step 3: Populate NewsFeatures
        NewsFeatures features = new NewsFeatures();

        features.setCharCount(stats.get("charCount").intValue());
        features.setWordCount(stats.get("wordCount").intValue());
        features.setSentenceCount(stats.get("sentenceCount").intValue());
        features.setAvgSentenceLength(stats.get("avgSentenceLength").doubleValue());
        features.setUppercaseWordCount(stats.get("uppercaseWordCount").intValue());
        features.setUppercaseRatio(stats.get("uppercaseRatio").doubleValue());
        features.setExclamationCount(stats.get("exclamationCount").intValue());
        features.setExclamationRatio(stats.get("exclamationRatio").doubleValue());
        features.setQuestionMarkCount(stats.get("questionMarkCount").intValue());
        features.setQuestionMarkRatio(stats.get("questionMarkRatio").doubleValue());
        features.setUrlCount(stats.get("urlCount").intValue());

        List<String> sensational  = kwResults.get("sensational");
        List<String> suspicious   = kwResults.get("suspicious");
        List<String> emotional    = kwResults.get("emotional");
        List<String> strongClaims = kwResults.get("strongClaims");

        features.setMatchedSensational(sensational);
        features.setMatchedSuspicious(suspicious);
        features.setMatchedEmotional(emotional);
        features.setMatchedStrongClaims(strongClaims);

        features.setSensationalKeywordCount(sensational.size());
        features.setSuspiciousKeywordCount(suspicious.size());
        features.setEmotionalKeywordCount(emotional.size());
        features.setStrongClaimCount(strongClaims.size());

        return features;
    }

    /** Expose text analyzer for stat display (used by CLI). */
    public TextAnalyzer getTextAnalyzer() { return textAnalyzer; }
}

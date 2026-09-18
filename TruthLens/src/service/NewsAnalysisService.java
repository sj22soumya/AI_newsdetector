package service;

import model.*;
import analyzer.*;
import classifier.*;
import util.InputValidator;
import util.FileManager;
import exception.InvalidNewsException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;

/**
 * Orchestrates the complete news analysis pipeline.
 * This is the primary entry point for all analysis operations.
 *
 * Pipeline:
 *   NewsArticle
 *     -> FeatureExtractor  (TextAnalyzer + KeywordAnalyzer)
 *     -> RuleBasedClassifier
 *     -> ExplanationService
 *     -> HistoryService
 *     -> PredictionResult
 *
 * Demonstrates: composition, service layer pattern, pipeline design.
 */
public class NewsAnalysisService {

    private final FeatureExtractor  featureExtractor;
    private final NewsClassifier    classifier;
    private final ExplanationService explanationService;
    private final HistoryService    historyService;

    public NewsAnalysisService(HistoryService historyService) {
        this.featureExtractor   = new FeatureExtractor();
        this.classifier         = new RuleBasedClassifier();   // polymorphism
        this.explanationService = new ExplanationService();
        this.historyService     = historyService;
    }

    /**
     * Analyse a NewsArticle and return the full PredictionResult.
     * Validates input, runs the pipeline, saves to history.
     *
     * @throws InvalidNewsException if headline or content is invalid.
     */
    public PredictionResult analyze(NewsArticle article) {
        // 1. Validate input
        InputValidator.validateHeadline(article.getHeadline());
        InputValidator.validateContent(article.getContent());

        // 2. Extract features
        NewsFeatures features = featureExtractor.extract(article);

        // 3. Classify
        PredictionResult result = classifier.classify(features);

        // 4. Generate explanation and recommendation
        explanationService.generateExplanation(result);

        // 5. Save to history
        AnalysisRecord record = new AnalysisRecord(
            generateId(),
            LocalDateTime.now(),
            article.getHeadline(),
            result.getRiskScore(),
            result.getClassification()
        );
        historyService.saveRecord(record);

        return result;
    }

    /**
     * Analyse content from a local text file.
     * Uses the same pipeline as direct text analysis.
     *
     * @param filePath Path to the .txt file.
     * @param headline The headline for this article.
     */
    public PredictionResult analyzeFile(String filePath, String headline) {
        InputValidator.validateFilePath(filePath);
        String content = FileManager.readFileContent(filePath);
        NewsArticle article = new NewsArticle(headline, content);
        return analyze(article);
    }

    /**
     * Get the text statistics for the last call (for display purposes).
     */
    public Map<String, Number> getLastStats(NewsArticle article) {
        return featureExtractor.getTextAnalyzer().analyze(article);
    }

    /** Get the text analyzer (for stat display). */
    public TextAnalyzer getTextAnalyzer() {
        return featureExtractor.getTextAnalyzer();
    }

    // Generate a short unique analysis ID
    private String generateId() {
        return "TL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

package model;

import java.util.List;
import java.util.ArrayList;

/**
 * Holds the complete analysis result for a news article.
 * Includes risk score, classification, detected signals, explanation, and recommendation.
 */
public class PredictionResult {

    // Classification labels
    public static final String CREDIBLE    = "POTENTIALLY CREDIBLE";
    public static final String VERIFY      = "NEEDS VERIFICATION";
    public static final String MISLEADING  = "POTENTIALLY MISLEADING";

    private int          riskScore;       // 0–100
    private String       classification;  // one of the constants above
    private double       confidence;      // 0.0–1.0  (assessment percentage)
    private List<String> detectedSignals; // human-readable list of triggered signals
    private String       explanation;     // paragraph explanation
    private String       recommendation;  // what the user should do

    // The features that produced this result (for reference)
    private NewsFeatures features;

    public PredictionResult() {
        this.detectedSignals = new ArrayList<>();
    }

    // --- Getters ---
    public int          getRiskScore()       { return riskScore; }
    public String       getClassification()  { return classification; }
    public double       getConfidence()      { return confidence; }
    public List<String> getDetectedSignals() { return detectedSignals; }
    public String       getExplanation()     { return explanation; }
    public String       getRecommendation()  { return recommendation; }
    public NewsFeatures getFeatures()        { return features; }

    // --- Setters ---
    public void setRiskScore(int riskScore)              { this.riskScore = Math.max(0, Math.min(100, riskScore)); }
    public void setClassification(String classification) { this.classification = classification; }
    public void setConfidence(double confidence)         { this.confidence = confidence; }
    public void setDetectedSignals(List<String> signals) { this.detectedSignals = signals; }
    public void setExplanation(String explanation)       { this.explanation = explanation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public void setFeatures(NewsFeatures features)       { this.features = features; }

    /** Convenience: confidence as a percentage string (e.g. "72%") */
    public String getConfidencePercent() {
        return String.format("%.0f%%", confidence * 100);
    }

    @Override
    public String toString() {
        return String.format("PredictionResult{score=%d, classification='%s', confidence=%.0f%%}",
            riskScore, classification, confidence * 100);
    }
}

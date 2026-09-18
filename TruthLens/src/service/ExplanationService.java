package service;

import model.PredictionResult;
import model.NewsFeatures;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates human-readable explanations and recommendations from a PredictionResult.
 * Only explains signals that were actually detected — never fabricates reasons.
 *
 * Demonstrates: single responsibility, string building, conditional logic.
 */
public class ExplanationService {

    private static final String DISCLAIMER =
        "This system provides an automated risk assessment based on textual signals.\n" +
        "It does not establish whether a news claim is factually true or false.";

    /**
     * Populate explanation and recommendation on the given result.
     * Mutates the result in place (signals are already set by classifier).
     */
    public void generateExplanation(PredictionResult result) {
        String explanation   = buildExplanation(result);
        String recommendation = buildRecommendation(result.getClassification());
        result.setExplanation(explanation);
        result.setRecommendation(recommendation);
    }

    /**
     * Build a paragraph explanation from the detected signals.
     */
    private String buildExplanation(PredictionResult result) {
        List<String> signals = result.getDetectedSignals();
        NewsFeatures f       = result.getFeatures();
        int score            = result.getRiskScore();

        StringBuilder sb = new StringBuilder();
        sb.append("Risk score of ").append(score).append("/100 was calculated based on the following:\n");

        for (String signal : signals) {
            sb.append("  \u2022 ").append(signal).append("\n");
        }

        // Add contextual note based on classification
        switch (result.getClassification()) {
            case PredictionResult.CREDIBLE:
                sb.append("\nThe text shows relatively few textual risk signals associated with "
                    + "misleading content.");
                break;
            case PredictionResult.VERIFY:
                sb.append("\nThe text contains some signals that warrant closer inspection. "
                    + "Consider verifying the claims using reliable sources.");
                break;
            case PredictionResult.MISLEADING:
                sb.append("\nThe text exhibits multiple textual patterns commonly associated with "
                    + "sensational, misleading, or low-credibility content.");
                break;
            default:
                break;
        }
        return sb.toString();
    }

    /**
     * Return an appropriate recommendation for the given classification.
     */
    private String buildRecommendation(String classification) {
        switch (classification) {
            case PredictionResult.CREDIBLE:
                return "Continue to verify important claims using reliable sources.";
            case PredictionResult.VERIFY:
                return "Consider checking the information against multiple reliable sources "
                     + "before sharing.";
            case PredictionResult.MISLEADING:
                return "This content contains several risk indicators commonly associated with "
                     + "misleading content. Verify the claims using reliable sources before sharing.";
            default:
                return "Exercise caution and verify claims before sharing.";
        }
    }

    /** Return the standard disclaimer text. */
    public String getDisclaimer() {
        return DISCLAIMER;
    }
}

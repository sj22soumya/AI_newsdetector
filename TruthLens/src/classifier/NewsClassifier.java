package classifier;

import model.NewsFeatures;
import model.PredictionResult;

/**
 * Abstract base class for news classifiers.
 * Defines the classification contract and provides the score-to-label mapping.
 *
 * Demonstrates: abstraction, inheritance, polymorphism.
 * Future classifiers (e.g., MLClassifier) can extend this without changing
 * the rest of the system — open/closed principle in action.
 */
public abstract class NewsClassifier {

    // Score thresholds
    protected static final int CREDIBLE_MAX   = 30;
    protected static final int VERIFY_MAX     = 60;

    /**
     * Core classification method — implemented by concrete subclasses.
     * Must return a fully populated PredictionResult.
     */
    public abstract PredictionResult classify(NewsFeatures features);

    /**
     * Map a numeric risk score to a classification label.
     * Shared by all subclasses (template method pattern).
     */
    protected String scoreToLabel(int score) {
        if (score <= CREDIBLE_MAX) return PredictionResult.CREDIBLE;
        if (score <= VERIFY_MAX)   return PredictionResult.VERIFY;
        return PredictionResult.MISLEADING;
    }

    /**
     * Calculate an assessment confidence value (0.0–1.0) from the risk score.
     * Represents how strongly the score aligns with the classification boundary.
     */
    protected double calculateConfidence(int score) {
        // Distance from the nearest boundary, normalised to 0–1
        if (score <= CREDIBLE_MAX) {
            // 0 = right at boundary (30), 1 = perfectly safe (0)
            return 1.0 - (score / (double) CREDIBLE_MAX) * 0.5;
        } else if (score <= VERIFY_MAX) {
            // Midpoint region — moderate confidence
            double midDist = Math.abs(score - ((CREDIBLE_MAX + VERIFY_MAX) / 2.0));
            return 0.5 + midDist / (VERIFY_MAX - CREDIBLE_MAX) * 0.3;
        } else {
            // 61–100: higher score = higher confidence in MISLEADING
            return 0.6 + (score - VERIFY_MAX) / (double)(100 - VERIFY_MAX) * 0.4;
        }
    }

    /**
     * Clamp a value to [0, max].
     */
    protected int clamp(int value, int max) {
        return Math.max(0, Math.min(value, max));
    }
}

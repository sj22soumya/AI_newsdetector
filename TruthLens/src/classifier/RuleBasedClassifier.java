package classifier;

import model.NewsFeatures;
import model.PredictionResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Rule-based implementation of NewsClassifier.
 * Calculates a deterministic risk score using weighted textual signals.
 *
 * Score breakdown (total max = 100):
 *   Sensational language    : 0–20
 *   Suspicious keywords     : 0–20
 *   Excessive capitalisation: 0–15
 *   Excessive punctuation   : 0–10
 *   Suspicious URLs         : 0–15
 *   Strong/emotional claims : 0–20
 *
 * Demonstrates: inheritance (extends NewsClassifier), method overriding,
 *               encapsulation, deterministic scoring.
 */
public class RuleBasedClassifier extends NewsClassifier {

    // Maximum points per category
    private static final int MAX_SENSATIONAL   = 20;
    private static final int MAX_SUSPICIOUS    = 20;
    private static final int MAX_CAPS          = 15;
    private static final int MAX_PUNCTUATION   = 10;
    private static final int MAX_URL           = 15;
    private static final int MAX_EMOTIONAL     = 20;

    @Override
    public PredictionResult classify(NewsFeatures features) {

        // --- Component scores ---
        int sensationalScore = scoreSensational(features);
        int suspiciousScore  = scoreSuspicious(features);
        int capsScore        = scoreCaps(features);
        int punctScore       = scorePunctuation(features);
        int urlScore         = scoreUrls(features);
        int emotionalScore   = scoreEmotional(features);

        int totalScore = sensationalScore + suspiciousScore + capsScore
                       + punctScore + urlScore + emotionalScore;
        totalScore = Math.min(100, totalScore); // hard cap

        // --- Build result ---
        PredictionResult result = new PredictionResult();
        result.setRiskScore(totalScore);
        result.setClassification(scoreToLabel(totalScore));
        result.setConfidence(calculateConfidence(totalScore));
        result.setFeatures(features);

        // --- Build detected signals list ---
        List<String> signals = buildSignals(features, sensationalScore, suspiciousScore,
                                            capsScore, punctScore, urlScore, emotionalScore);
        result.setDetectedSignals(signals);

        return result;
    }

    // ---- Individual scoring methods ----------------------------------------

    private int scoreSensational(NewsFeatures f) {
        int count = f.getSensationalKeywordCount();
        // 1 keyword → 8 pts; 2 → 14; 3+ → 20
        if (count == 0) return 0;
        if (count == 1) return clamp(8,  MAX_SENSATIONAL);
        if (count == 2) return clamp(14, MAX_SENSATIONAL);
        return MAX_SENSATIONAL;
    }

    private int scoreSuspicious(NewsFeatures f) {
        int count = f.getSuspiciousKeywordCount();
        if (count == 0) return 0;
        if (count == 1) return clamp(10, MAX_SUSPICIOUS);
        if (count == 2) return clamp(16, MAX_SUSPICIOUS);
        return MAX_SUSPICIOUS;
    }

    private int scoreCaps(NewsFeatures f) {
        double ratio = f.getUppercaseRatio();
        // ratio > 0.30 → max caps score; scaled linearly below that
        if (ratio <= 0.05) return 0;
        int score = (int) Math.round(ratio / 0.30 * MAX_CAPS);
        return clamp(score, MAX_CAPS);
    }

    private int scorePunctuation(NewsFeatures f) {
        // Combine exclamation and question mark ratios
        double combined = f.getExclamationRatio() + f.getQuestionMarkRatio();
        if (combined <= 0.02) return 0;
        int score = (int) Math.round(combined / 0.20 * MAX_PUNCTUATION);
        return clamp(score, MAX_PUNCTUATION);
    }

    private int scoreUrls(NewsFeatures f) {
        int urls = f.getUrlCount();
        // Presence of any URL in a short article is suspicious
        if (urls == 0) return 0;
        if (urls == 1) return clamp(7,  MAX_URL);
        if (urls == 2) return clamp(11, MAX_URL);
        return MAX_URL;
    }

    private int scoreEmotional(NewsFeatures f) {
        int emotionalCount   = f.getEmotionalKeywordCount();
        int strongClaimCount = f.getStrongClaimCount();
        int total = emotionalCount + strongClaimCount;
        if (total == 0) return 0;
        if (total == 1) return clamp(7,  MAX_EMOTIONAL);
        if (total == 2) return clamp(13, MAX_EMOTIONAL);
        if (total == 3) return clamp(17, MAX_EMOTIONAL);
        return MAX_EMOTIONAL;
    }

    // ---- Build human-readable detected signals list ------------------------

    private List<String> buildSignals(NewsFeatures f,
                                      int sensScore, int suspScore,
                                      int capsScore, int punctScore,
                                      int urlScore,  int emotScore) {
        List<String> signals = new ArrayList<>();

        if (sensScore > 0) {
            signals.add("Sensational language detected ("
                + f.getSensationalKeywordCount() + " indicator(s): "
                + String.join(", ", f.getMatchedSensational()) + ")");
        }
        if (suspScore > 0) {
            signals.add("Suspicious/clickbait phrases detected ("
                + f.getSuspiciousKeywordCount() + " phrase(s): "
                + String.join(", ", f.getMatchedSuspicious()) + ")");
        }
        if (capsScore > 0) {
            signals.add(String.format("Excessive capitalisation detected (%.0f%% of words in UPPERCASE)",
                f.getUppercaseRatio() * 100));
        }
        if (punctScore > 0) {
            signals.add(String.format("Excessive punctuation detected (%d exclamation mark(s), %d question mark(s))",
                f.getExclamationCount(), f.getQuestionMarkCount()));
        }
        if (urlScore > 0) {
            signals.add("URL(s) detected in content (" + f.getUrlCount() + " URL(s))");
        }
        if (f.getEmotionalKeywordCount() > 0) {
            signals.add("Emotional/alarming language detected ("
                + f.getEmotionalKeywordCount() + " term(s): "
                + String.join(", ", f.getMatchedEmotional()) + ")");
        }
        if (f.getStrongClaimCount() > 0) {
            signals.add("Strong claim indicators detected ("
                + f.getStrongClaimCount() + " term(s): "
                + String.join(", ", f.getMatchedStrongClaims()) + ")");
        }

        if (signals.isEmpty()) {
            signals.add("No significant risk signals detected");
        }
        return signals;
    }
}

package analyzer;

import util.TextUtils;
import java.util.*;

/**
 * Maintains categorised keyword lists and detects matches within article text.
 * All keyword lists are centralised here — easy to extend.
 *
 * Demonstrates: collections (Set, List, Map), encapsulation, extensibility.
 */
public class KeywordAnalyzer {

    // ---- Sensational Terms ------------------------------------------------
    private static final Set<String> SENSATIONAL_KEYWORDS = new LinkedHashSet<>(Arrays.asList(
        "SHOCKING", "AMAZING", "BREAKING", "SECRET", "EXPOSED", "URGENT",
        "UNBELIEVABLE", "YOU WON'T BELIEVE", "MIRACLE", "BOMBSHELL", "EXPLOSIVE",
        "STUNNING", "EXCLUSIVE", "ALERT", "LEAKED", "SCANDAL", "OUTRAGEOUS",
        "UNPRECEDENTED", "TERRIFYING TRUTH", "MUST READ", "BRACE YOURSELF"
    ));

    // ---- Suspicious / Clickbait Phrases ------------------------------------
    private static final Set<String> SUSPICIOUS_KEYWORDS = new LinkedHashSet<>(Arrays.asList(
        "CLICK HERE", "SHARE NOW", "FORWARD THIS", "THEY DON'T WANT YOU TO KNOW",
        "GUARANTEED", "100% PROOF", "DOCTORS HATE THIS", "ONE WEIRD TRICK",
        "THIS WILL SHOCK YOU", "WAKE UP", "SPREAD THE WORD", "BANNED FROM",
        "CENSORED", "SUPPRESSED", "BIG PHARMA", "DEEP STATE", "FAKE NEWS",
        "MAINSTREAM MEDIA WON'T TELL YOU", "DO YOUR RESEARCH"
    ));

    // ---- Emotional / Sensational Tone Words --------------------------------
    private static final Set<String> EMOTIONAL_KEYWORDS = new LinkedHashSet<>(Arrays.asList(
        "HORRIFYING", "SCARY", "TERRIFYING", "INCREDIBLE", "OUTRAGE", "DISASTER",
        "CATASTROPHE", "TRAGEDY", "CRISIS", "PANIC", "CHAOS", "DEVASTATING",
        "HEARTBREAKING", "RAGE", "FURY", "SHOCKING TRUTH", "HORRIFIC",
        "UNTHINKABLE", "APOCALYPSE", "COLLAPSE"
    ));

    // ---- Strong Claim Indicators -------------------------------------------
    private static final Set<String> STRONG_CLAIM_KEYWORDS = new LinkedHashSet<>(Arrays.asList(
        "PROVEN", "CONFIRMED", "OFFICIAL", "SCIENTISTS SAY", "EXPERTS REVEAL",
        "STUDY SHOWS", "RESEARCH CONFIRMS", "100%", "ALWAYS", "NEVER",
        "EVERYONE KNOWS", "FACT:", "TRUTH:", "THE TRUTH ABOUT",
        "FINALLY REVEALED", "IT'S A FACT", "UNDENIABLE"
    ));

    /**
     * Scan the provided text and return a map of category → matched keywords.
     * The map keys are category names; values are lists of matched keyword strings.
     */
    public Map<String, List<String>> analyze(String text) {
        String upperText = text.toUpperCase();

        Map<String, List<String>> results = new LinkedHashMap<>();
        results.put("sensational",  findMatches(upperText, SENSATIONAL_KEYWORDS));
        results.put("suspicious",   findMatches(upperText, SUSPICIOUS_KEYWORDS));
        results.put("emotional",    findMatches(upperText, EMOTIONAL_KEYWORDS));
        results.put("strongClaims", findMatches(upperText, STRONG_CLAIM_KEYWORDS));
        return results;
    }

    /**
     * Return the full set for a given category (for UI display).
     */
    public static Set<String> getSensationalKeywords()  { return Collections.unmodifiableSet(SENSATIONAL_KEYWORDS); }
    public static Set<String> getSuspiciousKeywords()   { return Collections.unmodifiableSet(SUSPICIOUS_KEYWORDS); }
    public static Set<String> getEmotionalKeywords()    { return Collections.unmodifiableSet(EMOTIONAL_KEYWORDS); }
    public static Set<String> getStrongClaimKeywords()  { return Collections.unmodifiableSet(STRONG_CLAIM_KEYWORDS); }

    // ---- Private helpers ---------------------------------------------------

    private List<String> findMatches(String upperText, Set<String> keywords) {
        List<String> matched = new ArrayList<>();
        for (String kw : keywords) {
            if (upperText.contains(kw)) {
                matched.add(kw);
            }
        }
        return matched;
    }
}

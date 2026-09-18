package model;

import java.util.List;
import java.util.ArrayList;

/**
 * Holds all numerically extracted features from a NewsArticle.
 * Used by the classifier to compute a risk score.
 */
public class NewsFeatures {

    private int    wordCount;
    private int    sentenceCount;
    private int    charCount;
    private double avgSentenceLength;
    private int    uppercaseWordCount;
    private double uppercaseRatio;
    private int    exclamationCount;
    private double exclamationRatio;
    private int    questionMarkCount;
    private double questionMarkRatio;
    private int    urlCount;
    private int    suspiciousKeywordCount;
    private int    sensationalKeywordCount;
    private int    emotionalKeywordCount;
    private int    strongClaimCount;

    // The actual matched keywords (for explanation purposes)
    private List<String> matchedSensational  = new ArrayList<>();
    private List<String> matchedSuspicious   = new ArrayList<>();
    private List<String> matchedEmotional    = new ArrayList<>();
    private List<String> matchedStrongClaims = new ArrayList<>();

    // Default constructor
    public NewsFeatures() {}

    // --- Getters ---
    public int    getWordCount()              { return wordCount; }
    public int    getSentenceCount()          { return sentenceCount; }
    public int    getCharCount()              { return charCount; }
    public double getAvgSentenceLength()      { return avgSentenceLength; }
    public int    getUppercaseWordCount()     { return uppercaseWordCount; }
    public double getUppercaseRatio()         { return uppercaseRatio; }
    public int    getExclamationCount()       { return exclamationCount; }
    public double getExclamationRatio()       { return exclamationRatio; }
    public int    getQuestionMarkCount()      { return questionMarkCount; }
    public double getQuestionMarkRatio()      { return questionMarkRatio; }
    public int    getUrlCount()               { return urlCount; }
    public int    getSuspiciousKeywordCount() { return suspiciousKeywordCount; }
    public int    getSensationalKeywordCount(){ return sensationalKeywordCount; }
    public int    getEmotionalKeywordCount()  { return emotionalKeywordCount; }
    public int    getStrongClaimCount()       { return strongClaimCount; }
    public List<String> getMatchedSensational()  { return matchedSensational; }
    public List<String> getMatchedSuspicious()   { return matchedSuspicious; }
    public List<String> getMatchedEmotional()    { return matchedEmotional; }
    public List<String> getMatchedStrongClaims() { return matchedStrongClaims; }

    // --- Setters ---
    public void setWordCount(int wordCount)                     { this.wordCount = wordCount; }
    public void setSentenceCount(int sentenceCount)             { this.sentenceCount = sentenceCount; }
    public void setCharCount(int charCount)                     { this.charCount = charCount; }
    public void setAvgSentenceLength(double avgSentenceLength)  { this.avgSentenceLength = avgSentenceLength; }
    public void setUppercaseWordCount(int uppercaseWordCount)   { this.uppercaseWordCount = uppercaseWordCount; }
    public void setUppercaseRatio(double uppercaseRatio)        { this.uppercaseRatio = uppercaseRatio; }
    public void setExclamationCount(int exclamationCount)       { this.exclamationCount = exclamationCount; }
    public void setExclamationRatio(double exclamationRatio)    { this.exclamationRatio = exclamationRatio; }
    public void setQuestionMarkCount(int questionMarkCount)     { this.questionMarkCount = questionMarkCount; }
    public void setQuestionMarkRatio(double questionMarkRatio)  { this.questionMarkRatio = questionMarkRatio; }
    public void setUrlCount(int urlCount)                       { this.urlCount = urlCount; }
    public void setSuspiciousKeywordCount(int c)                { this.suspiciousKeywordCount = c; }
    public void setSensationalKeywordCount(int c)               { this.sensationalKeywordCount = c; }
    public void setEmotionalKeywordCount(int c)                 { this.emotionalKeywordCount = c; }
    public void setStrongClaimCount(int strongClaimCount)       { this.strongClaimCount = strongClaimCount; }
    public void setMatchedSensational(List<String> list)        { this.matchedSensational = list; }
    public void setMatchedSuspicious(List<String> list)         { this.matchedSuspicious = list; }
    public void setMatchedEmotional(List<String> list)          { this.matchedEmotional = list; }
    public void setMatchedStrongClaims(List<String> list)       { this.matchedStrongClaims = list; }

    @Override
    public String toString() {
        return String.format(
            "NewsFeatures{words=%d, sentences=%d, upperRatio=%.2f, exclRatio=%.2f, " +
            "urls=%d, sensational=%d, suspicious=%d, emotional=%d, strongClaims=%d}",
            wordCount, sentenceCount, uppercaseRatio, exclamationRatio,
            urlCount, sensationalKeywordCount, suspiciousKeywordCount,
            emotionalKeywordCount, strongClaimCount);
    }
}

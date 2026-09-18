import model.*;
import service.*;
import exception.*;
import util.InputValidator;

import java.util.List;

/**
 * TruthLens Automated Test Suite
 * Tests all major functional modules without requiring user interaction.
 *
 * Run with:
 *   java -cp out TruthLensTest
 */
public class TruthLensTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  TRUTHLENS — AUTOMATED TEST SUITE");
        System.out.println("============================================================\n");

        // Run all test groups
        testValidArticle();
        testEmptyHeadline();
        testEmptyContent();
        testShortContent();
        testSensationalArticle();
        testNormalArticle();
        testUrlDetection();
        testExcessiveCaps();
        testExcessivePunctuation();
        testSuspiciousKeywords();
        testHistorySave();
        testHistoryLoad();
        testHistorySearch();
        testHistoryDelete();
        testHistoryClear();
        testInvalidMenuInput();
        testInvalidFilePath();

        System.out.println("\n============================================================");
        System.out.printf("  RESULTS: %d passed | %d failed | %d total%n",
            passed, failed, passed + failed);
        System.out.println("============================================================");
        if (failed == 0) {
            System.out.println("  ALL TESTS PASSED ✓");
        } else {
            System.out.println("  SOME TESTS FAILED. See details above.");
        }
    }

    // ========================================================================
    //  TEST CASES
    // ========================================================================

    private static void testValidArticle() {
        printTestGroup("TEST 1 — Valid Article Analysis");
        try {
            HistoryService hs = new HistoryService();
            NewsAnalysisService svc = new NewsAnalysisService(hs);
            NewsArticle article = new NewsArticle(
                "New Study Examines Coffee and Heart Health",
                "Researchers at a university published a five-year study on coffee consumption " +
                "and cardiovascular health. The study followed 12,000 participants aged 40-70. " +
                "Results suggest a modest association. Researchers caution that correlation " +
                "does not imply causation and further studies are needed."
            );
            PredictionResult result = svc.analyze(article);
            assertNotNull("Result not null", result);
            assertTrue("Risk score 0-100", result.getRiskScore() >= 0 && result.getRiskScore() <= 100);
            assertNotNull("Classification set", result.getClassification());
            assertNotNull("Explanation set", result.getExplanation());
            assertNotNull("Recommendation set", result.getRecommendation());
            assertNotNull("Signals not null", result.getDetectedSignals());
            pass("Valid article analysis completed — Score: " + result.getRiskScore()
                + ", Classification: " + result.getClassification());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testEmptyHeadline() {
        printTestGroup("TEST 2 — Empty Headline");
        try {
            InputValidator.validateHeadline("");
            fail("Expected InvalidNewsException for empty headline");
        } catch (InvalidNewsException e) {
            pass("Correctly threw InvalidNewsException: " + e.getMessage());
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getClass().getSimpleName());
        }
    }

    private static void testEmptyContent() {
        printTestGroup("TEST 3 — Empty Content");
        try {
            InputValidator.validateContent("");
            fail("Expected InvalidNewsException for empty content");
        } catch (InvalidNewsException e) {
            pass("Correctly threw InvalidNewsException: " + e.getMessage());
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getClass().getSimpleName());
        }
    }

    private static void testShortContent() {
        printTestGroup("TEST 4 — Very Short Content");
        try {
            InputValidator.validateContent("Short");
            fail("Expected InvalidNewsException for short content");
        } catch (InvalidNewsException e) {
            pass("Correctly rejected short content: " + e.getMessage());
        } catch (Exception e) {
            fail("Wrong exception type: " + e.getClass().getSimpleName());
        }
    }

    private static void testSensationalArticle() {
        printTestGroup("TEST 5 — Sensational Article (Expects HIGH score)");
        try {
            HistoryService hs = new HistoryService();
            NewsAnalysisService svc = new NewsAnalysisService(hs);
            NewsArticle article = new NewsArticle(
                "SHOCKING SECRET EXPOSED — They Don't Want You to Know!!!",
                "BREAKING: The DEEP STATE has been EXPOSED. This UNBELIEVABLE MIRACLE CURE has been " +
                "CENSORED by BIG PHARMA! SHARE NOW before it gets BANNED! You WON'T BELIEVE the truth " +
                "THEY have been HIDING. GUARANTEED 100% PROOF of a massive conspiracy!!! " +
                "WAKE UP PEOPLE! Click here: http://fake-news.example.com"
            );
            PredictionResult result = svc.analyze(article);
            assertTrue("Sensational article should score >= 50", result.getRiskScore() >= 50);
            assertFalse("Should have detected signals", result.getDetectedSignals().isEmpty());
            pass("Sensational article scored: " + result.getRiskScore()
                + " (" + result.getClassification() + ")");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testNormalArticle() {
        printTestGroup("TEST 6 — Normal/Credible Article (Expects LOW score)");
        try {
            HistoryService hs = new HistoryService();
            NewsAnalysisService svc = new NewsAnalysisService(hs);
            NewsArticle article = new NewsArticle(
                "City Council Approves Infrastructure Budget",
                "The city council approved a new infrastructure budget for road maintenance and " +
                "public transport upgrades. The council voted seven to two in favour of the proposal. " +
                "The budget will be allocated over three financial years. A spokesperson confirmed " +
                "that work is expected to begin in the first quarter of the upcoming year."
            );
            PredictionResult result = svc.analyze(article);
            assertTrue("Normal article should score <= 30", result.getRiskScore() <= 30);
            pass("Normal article scored: " + result.getRiskScore()
                + " (" + result.getClassification() + ")");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testUrlDetection() {
        printTestGroup("TEST 7 — Article with URLs");
        try {
            HistoryService hs = new HistoryService();
            NewsAnalysisService svc = new NewsAnalysisService(hs);
            NewsArticle article = new NewsArticle(
                "Important Health Alert",
                "Read the full story at http://example.com and https://another-site.example.org " +
                "for the complete details. Researchers have published their findings online. " +
                "The study is available at the link provided above for public review."
            );
            PredictionResult result = svc.analyze(article);
            assertTrue("URL article should have URL score", result.getRiskScore() > 0);
            pass("URL article scored: " + result.getRiskScore());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testExcessiveCaps() {
        printTestGroup("TEST 8 — Excessive Capitalisation");
        try {
            HistoryService hs = new HistoryService();
            NewsAnalysisService svc = new NewsAnalysisService(hs);
            NewsArticle article = new NewsArticle(
                "GOVERNMENT OFFICIAL SAYS ECONOMY IS STRONG",
                "THE GOVERNMENT HAS CONFIRMED THAT THE ECONOMY IS IN EXCELLENT SHAPE. " +
                "OFFICIALS SAY THAT GDP GROWTH HAS EXCEEDED ALL EXPECTATIONS. " +
                "THE PRIME MINISTER CALLED IT AN OUTSTANDING ACHIEVEMENT. " +
                "CITIZENS ARE ENCOURAGED TO REMAIN CONFIDENT IN THE SYSTEM."
            );
            PredictionResult result = svc.analyze(article);
            assertTrue("High caps article should score > 10", result.getRiskScore() > 10);
            pass("Caps article scored: " + result.getRiskScore());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testExcessivePunctuation() {
        printTestGroup("TEST 9 — Excessive Punctuation");
        try {
            HistoryService hs = new HistoryService();
            NewsAnalysisService svc = new NewsAnalysisService(hs);
            NewsArticle article = new NewsArticle(
                "Can you believe this happened?!",
                "This is absolutely unbelievable!!! Can this really be true?! " +
                "You will not believe what happened next! Why is nobody talking about this?! " +
                "Everyone needs to know about this right now!!! The story must be shared!!! " +
                "How can this be allowed to continue?!!"
            );
            PredictionResult result = svc.analyze(article);
            assertTrue("Punct article should score > 5", result.getRiskScore() > 5);
            pass("Punctuation article scored: " + result.getRiskScore());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testSuspiciousKeywords() {
        printTestGroup("TEST 10 — Suspicious Keywords");
        try {
            HistoryService hs = new HistoryService();
            NewsAnalysisService svc = new NewsAnalysisService(hs);
            NewsArticle article = new NewsArticle(
                "They Don't Want You To Know About This Guaranteed Cure",
                "They don't want you to know about this guaranteed miracle treatment. " +
                "Do your research and wake up to the truth that doctors hate about " +
                "this one weird trick that has been suppressed. Spread the word now " +
                "and share this with everyone before it gets censored."
            );
            PredictionResult result = svc.analyze(article);
            assertTrue("Suspicious keyword article should score > 20", result.getRiskScore() > 20);
            pass("Suspicious keywords article scored: " + result.getRiskScore());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testHistorySave() {
        printTestGroup("TEST 11 — History Save");
        try {
            HistoryService hs = new HistoryService();
            int countBefore = hs.getRecordCount();
            NewsAnalysisService svc = new NewsAnalysisService(hs);
            svc.analyze(new NewsArticle(
                "History Save Test Headline",
                "This is a test article to verify that analysis records are saved correctly " +
                "to the history file. It contains enough text to pass validation checks."
            ));
            int countAfter = hs.getRecordCount();
            assertTrue("Record count should increase by 1", countAfter == countBefore + 1);
            pass("History save works correctly. Count: " + countBefore + " -> " + countAfter);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testHistoryLoad() {
        printTestGroup("TEST 12 — History Load (Persistence)");
        try {
            // Create a new HistoryService instance (simulates app restart)
            HistoryService hs1 = new HistoryService();
            NewsAnalysisService svc = new NewsAnalysisService(hs1);
            svc.analyze(new NewsArticle(
                "Persistence Test Article",
                "This article is used to test that history records persist across application " +
                "restarts by loading from the data file on startup."
            ));
            int savedCount = hs1.getRecordCount();

            // New instance simulates restart
            HistoryService hs2 = new HistoryService();
            int loadedCount = hs2.getRecordCount();

            assertTrue("Loaded count should equal saved count (>= " + savedCount + ")",
                loadedCount >= savedCount);
            pass("History persistence works. Saved: " + savedCount + ", Loaded: " + loadedCount);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testHistorySearch() {
        printTestGroup("TEST 13 — History Search");
        try {
            HistoryService hs = new HistoryService();
            NewsAnalysisService svc = new NewsAnalysisService(hs);
            svc.analyze(new NewsArticle(
                "Unique Zebra Migration Pattern Research",
                "Scientists have observed unique zebra migration patterns in the southern " +
                "African savanna. The research team documented seasonal movements across " +
                "several hundred kilometres of terrain."
            ));
            List<AnalysisRecord> results = hs.searchByHeadline("Zebra");
            assertTrue("Should find record with 'Zebra' in headline", results.size() >= 1);
            pass("History search found " + results.size() + " record(s) for 'Zebra'");

            List<AnalysisRecord> noResults = hs.searchByHeadline("XYZNOTFOUND123");
            assertTrue("Empty search should return empty list", noResults.isEmpty());
            pass("History search correctly returned empty for missing keyword");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testHistoryDelete() {
        printTestGroup("TEST 14 — History Delete");
        try {
            HistoryService hs = new HistoryService();
            NewsAnalysisService svc = new NewsAnalysisService(hs);
            svc.analyze(new NewsArticle(
                "Article For Deletion Test",
                "This article is specifically created to test the delete functionality " +
                "in the history service. It will be deleted immediately after creation."
            ));
            List<AnalysisRecord> all = hs.getAllRecords();
            assertTrue("Should have at least 1 record to delete", !all.isEmpty());

            String idToDelete = all.get(all.size() - 1).getId();
            boolean deleted = hs.deleteRecord(idToDelete);
            assertTrue("Delete should return true", deleted);

            boolean notFound = !hs.deleteRecord("NOTEXIST");
            assertTrue("Delete non-existent should return false", notFound);
            pass("History delete works correctly");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testHistoryClear() {
        printTestGroup("TEST 15 — History Clear");
        try {
            HistoryService hs = new HistoryService();
            hs.clearHistory();
            assertTrue("History should be empty after clear", hs.getRecordCount() == 0);
            pass("History clear works correctly");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    private static void testInvalidMenuInput() {
        printTestGroup("TEST 16 — Invalid Menu Input");
        try {
            InputValidator.validateMenuChoice("abc", 1, 8);
            fail("Expected InvalidInputException for non-numeric input");
        } catch (InvalidInputException e) {
            pass("Correctly rejected non-numeric input: " + e.getMessage());
        }
        try {
            InputValidator.validateMenuChoice("99", 1, 8);
            fail("Expected InvalidInputException for out-of-range input");
        } catch (InvalidInputException e) {
            pass("Correctly rejected out-of-range input: " + e.getMessage());
        }
        try {
            InputValidator.validateMenuChoice("", 1, 8);
            fail("Expected InvalidInputException for empty input");
        } catch (InvalidInputException e) {
            pass("Correctly rejected empty input: " + e.getMessage());
        }
    }

    private static void testInvalidFilePath() {
        printTestGroup("TEST 17 — Invalid File Path");
        try {
            InputValidator.validateFilePath("this/path/does/not/exist.txt");
            fail("Expected InvalidInputException for non-existent file");
        } catch (InvalidInputException e) {
            pass("Correctly rejected non-existent file: " + e.getMessage());
        }
        try {
            InputValidator.validateFilePath("");
            fail("Expected InvalidInputException for empty path");
        } catch (InvalidInputException e) {
            pass("Correctly rejected empty file path: " + e.getMessage());
        }
    }

    // ========================================================================
    //  ASSERTION HELPERS
    // ========================================================================

    private static void assertNotNull(String label, Object obj) {
        if (obj == null) throw new AssertionError(label + " — expected non-null");
    }

    private static void assertTrue(String label, boolean condition) {
        if (!condition) throw new AssertionError(label + " — condition was false");
    }

    private static void assertFalse(String label, boolean condition) {
        if (condition) throw new AssertionError(label + " — condition was true (expected false)");
    }

    private static void pass(String message) {
        passed++;
        System.out.println("  [PASS] " + message);
    }

    private static void fail(String message) {
        failed++;
        System.out.println("  [FAIL] " + message);
    }

    private static void printTestGroup(String title) {
        System.out.println("\n  " + title);
        System.out.println("  " + "-".repeat(Math.min(title.length(), 50)));
    }
}

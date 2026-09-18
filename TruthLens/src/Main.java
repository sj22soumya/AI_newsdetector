import model.*;
import service.*;
import exception.*;
import util.InputValidator;

import java.util.List;
import java.util.Scanner;
import java.util.Map;

/**
 * TruthLens — Intelligent News Credibility Analyzer
 * Main entry point. Provides the interactive command-line interface.
 *
 * Responsibilities:
 *   - Display menus
 *   - Read user input
 *   - Delegate to services
 *   - Display results
 *   - Handle exceptions gracefully
 *
 * Demonstrates: CLI design, exception handling, service delegation.
 */
public class Main {

    // ---- UI Constants -------------------------------------------------------
    private static final String LINE_DOUBLE = "==================================================";
    private static final String LINE_SINGLE = "--------------------------------------------------";
    private static final String APP_NAME    = "TRUTHLENS";
    private static final String APP_TITLE   = "INTELLIGENT NEWS CREDIBILITY ANALYZER";
    private static final String VERSION     = "v1.0  |  Educational Tool";
    private static final String DISCLAIMER  =
        "This system provides an automated risk assessment\n" +
        "based on textual signals. It does not establish\n" +
        "whether a claim is factually true or false.";

    // ---- Services ----------------------------------------------------------
    private final HistoryService     historyService;
    private final NewsAnalysisService analysisService;
    private final Scanner            scanner;

    public Main() {
        this.historyService  = new HistoryService();
        this.analysisService = new NewsAnalysisService(historyService);
        this.scanner         = new Scanner(System.in);
    }

    // ========================================================================
    //  ENTRY POINT
    // ========================================================================

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {
        showBanner();
        boolean running = true;
        while (running) {
            showMainMenu();
            String input = scanner.nextLine().trim();
            try {
                int choice = InputValidator.validateMenuChoice(input, 1, 8);
                switch (choice) {
                    case 1: analyzeNewsText();    break;
                    case 2: analyzeNewsFile();    break;
                    case 3: viewHistory();        break;
                    case 4: searchHistory();      break;
                    case 5: deleteHistoryRecord();break;
                    case 6: clearHistory();       break;
                    case 7: showAbout();          break;
                    case 8: running = false;      break;
                    default: break;
                }
            } catch (InvalidInputException e) {
                printError("Invalid input: " + e.getMessage());
            } catch (Exception e) {
                printError("Unexpected error: " + e.getMessage());
            }
        }
        showGoodbye();
        scanner.close();
    }

    // ========================================================================
    //  OPTION 1 — ANALYSE NEWS TEXT
    // ========================================================================

    private void analyzeNewsText() {
        printSectionHeader("ANALYZE NEWS ARTICLE");

        System.out.print("  Enter headline:\n  > ");
        String headline = scanner.nextLine().trim();

        System.out.println("  Enter article text (press Enter twice when done):");
        System.out.print("  > ");
        StringBuilder sb = new StringBuilder();
        String line;
        String prev = "";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.isEmpty() && prev.isEmpty()) break; // two consecutive blank lines = done
            sb.append(line).append(" ");
            prev = line;
        }
        String content = sb.toString().trim();

        runAnalysis(headline, content, null);
    }

    // ========================================================================
    //  OPTION 2 — ANALYSE NEWS FILE
    // ========================================================================

    private void analyzeNewsFile() {
        printSectionHeader("ANALYZE NEWS FROM FILE");

        System.out.print("  Enter headline for this article:\n  > ");
        String headline = scanner.nextLine().trim();

        System.out.print("  Enter file path (.txt):\n  > ");
        String filePath = scanner.nextLine().trim();

        runAnalysis(headline, null, filePath);
    }

    // ========================================================================
    //  SHARED ANALYSIS RUNNER
    // ========================================================================

    private void runAnalysis(String headline, String content, String filePath) {
        System.out.println();
        System.out.println(LINE_SINGLE);
        System.out.println("              ANALYSIS IN PROGRESS...");
        System.out.println(LINE_SINGLE);

        try {
            PredictionResult result;
            NewsArticle article;

            if (filePath != null) {
                result  = analysisService.analyzeFile(filePath, headline);
                // Re-create article for stat display
                String fileContent = util.FileManager.readFileContent(filePath);
                article = new NewsArticle(headline, fileContent);
            } else {
                article = new NewsArticle(headline, content);
                result  = analysisService.analyze(article);
            }

            // Get text stats for display
            Map<String, Number> stats = analysisService.getLastStats(article);
            displayAnalysisResult(result, stats);

        } catch (InvalidNewsException e) {
            printError("Validation error [" + e.getField() + "]: " + e.getMessage());
        } catch (InvalidInputException e) {
            printError("Input error: " + e.getMessage());
        } catch (exception.FileStorageException e) {
            printError("File error: " + e.getMessage());
        }
    }

    // ========================================================================
    //  DISPLAY ANALYSIS RESULT
    // ========================================================================

    private void displayAnalysisResult(PredictionResult result, Map<String, Number> stats) {
        System.out.println();
        System.out.println(LINE_DOUBLE);
        System.out.println("                 ANALYSIS RESULT");
        System.out.println(LINE_DOUBLE);
        System.out.println();

        System.out.printf("  Classification : %s%n", result.getClassification());
        System.out.printf("  Risk Score     : %d/100%n", result.getRiskScore());
        System.out.printf("  Assessment     : %s%n%n", result.getConfidencePercent());

        // Text statistics
        System.out.println("  Text Statistics:");
        System.out.println(analysisService.getTextAnalyzer().formatStats(stats));

        // Detected signals
        System.out.println("  Detected Signals:");
        for (String signal : result.getDetectedSignals()) {
            System.out.println("    \u2022 " + signal);
        }

        // Explanation
        System.out.println();
        System.out.println(LINE_SINGLE);
        System.out.println("  EXPLANATION:");
        System.out.println(LINE_SINGLE);
        System.out.println(indent(result.getExplanation(), "  "));

        // Recommendation
        System.out.println();
        System.out.println(LINE_SINGLE);
        System.out.println("  RECOMMENDATION:");
        System.out.println(LINE_SINGLE);
        System.out.println("  " + result.getRecommendation());

        // Disclaimer
        System.out.println();
        System.out.println(LINE_SINGLE);
        System.out.println("  DISCLAIMER:");
        System.out.println(LINE_SINGLE);
        System.out.println(indent(DISCLAIMER, "  "));
        System.out.println();
        System.out.println(LINE_DOUBLE);

        System.out.println();
        System.out.print("  Press Enter to return to the main menu...");
        scanner.nextLine();
    }

    // ========================================================================
    //  OPTION 3 — VIEW HISTORY
    // ========================================================================

    private void viewHistory() {
        printSectionHeader("ANALYSIS HISTORY");
        List<AnalysisRecord> records = historyService.getAllRecords();
        if (records.isEmpty()) {
            System.out.println("  No analysis records found.");
        } else {
            System.out.printf("  Total records: %d%n%n", records.size());
            System.out.printf("  %-12s %-20s %-6s %-22s %s%n",
                "ID", "Timestamp", "Score", "Classification", "Headline");
            System.out.println("  " + LINE_SINGLE);
            for (AnalysisRecord r : records) {
                System.out.printf("  %-12s %-20s %-6d %-22s %s%n",
                    r.getId(),
                    r.getFormattedTimestamp(),
                    r.getRiskScore(),
                    r.getClassification(),
                    util.TextUtils.truncate(r.getHeadline(), 40));
            }
        }
        System.out.println();
        System.out.print("  Press Enter to return...");
        scanner.nextLine();
    }

    // ========================================================================
    //  OPTION 4 — SEARCH HISTORY
    // ========================================================================

    private void searchHistory() {
        printSectionHeader("SEARCH HISTORY");
        System.out.print("  Enter keyword to search in headlines:\n  > ");
        String keyword = scanner.nextLine().trim();

        try {
            InputValidator.validateSearchKeyword(keyword);
        } catch (InvalidInputException e) {
            printError(e.getMessage());
            return;
        }

        List<AnalysisRecord> results = historyService.searchByHeadline(keyword);
        if (results.isEmpty()) {
            System.out.println("  No records found for keyword: \"" + keyword + "\"");
        } else {
            System.out.printf("  Found %d record(s) matching \"%s\":%n%n", results.size(), keyword);
            for (AnalysisRecord r : results) {
                System.out.println("  " + r.toString());
            }
        }
        System.out.println();
        System.out.print("  Press Enter to return...");
        scanner.nextLine();
    }

    // ========================================================================
    //  OPTION 5 — DELETE RECORD
    // ========================================================================

    private void deleteHistoryRecord() {
        printSectionHeader("DELETE HISTORY RECORD");
        System.out.print("  Enter the Record ID to delete (e.g., TL-XXXXXXXX):\n  > ");
        String id = scanner.nextLine().trim();

        try {
            InputValidator.validateRecordId(id);
        } catch (InvalidInputException e) {
            printError(e.getMessage());
            return;
        }

        boolean deleted = historyService.deleteRecord(id);
        if (deleted) {
            System.out.println("  Record " + id + " deleted successfully.");
        } else {
            System.out.println("  Record not found: " + id);
        }
        System.out.println();
        System.out.print("  Press Enter to return...");
        scanner.nextLine();
    }

    // ========================================================================
    //  OPTION 6 — CLEAR HISTORY
    // ========================================================================

    private void clearHistory() {
        printSectionHeader("CLEAR ALL HISTORY");
        int count = historyService.getRecordCount();
        if (count == 0) {
            System.out.println("  History is already empty.");
            System.out.println();
            System.out.print("  Press Enter to return...");
            scanner.nextLine();
            return;
        }
        System.out.printf("  This will permanently delete %d record(s).%n", count);
        System.out.print("  Are you sure? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes") || confirm.equals("y")) {
            historyService.clearHistory();
            System.out.println("  All history records have been cleared.");
        } else {
            System.out.println("  Operation cancelled.");
        }
        System.out.println();
        System.out.print("  Press Enter to return...");
        scanner.nextLine();
    }

    // ========================================================================
    //  OPTION 7 — ABOUT
    // ========================================================================

    private void showAbout() {
        System.out.println();
        System.out.println(LINE_DOUBLE);
        System.out.println("                  ABOUT TRUTHLENS");
        System.out.println(LINE_DOUBLE);
        System.out.println();
        System.out.println("  Project        : TruthLens");
        System.out.println("  Version        : " + VERSION);
        System.out.println("  Purpose        : Educational news credibility risk analysis");
        System.out.println("  Technology     : Java (JDK 17+)");
        System.out.println();
        System.out.println("  Java Concepts Demonstrated:");
        System.out.println("    \u2022 Object-Oriented Programming (OOP)");
        System.out.println("    \u2022 Encapsulation");
        System.out.println("    \u2022 Inheritance  (NewsClassifier -> RuleBasedClassifier)");
        System.out.println("    \u2022 Polymorphism (classifier used via abstract type)");
        System.out.println("    \u2022 Abstraction  (abstract classes and interfaces)");
        System.out.println("    \u2022 Interfaces   (IRepository<T>)");
        System.out.println("    \u2022 Collections  (List, Map, Set)");
        System.out.println("    \u2022 Generics     (IRepository<T>, FileHistoryRepository)");
        System.out.println("    \u2022 Exception Handling (try/catch/custom exceptions)");
        System.out.println("    \u2022 File Handling (read/write, persistence)");
        System.out.println();
        System.out.println("  Disclaimer:");
        System.out.println(indent(DISCLAIMER, "    "));
        System.out.println();
        System.out.println(LINE_DOUBLE);
        System.out.println();
        System.out.print("  Press Enter to return...");
        scanner.nextLine();
    }

    // ========================================================================
    //  UI HELPERS
    // ========================================================================

    private void showBanner() {
        System.out.println();
        System.out.println(LINE_DOUBLE);
        System.out.println("                   " + APP_NAME);
        System.out.println("        " + APP_TITLE);
        System.out.println("              " + VERSION);
        System.out.println(LINE_DOUBLE);
        System.out.println();
    }

    private void showMainMenu() {
        System.out.println(LINE_DOUBLE);
        System.out.println("                   MAIN MENU");
        System.out.println(LINE_DOUBLE);
        System.out.println();
        System.out.println("  1. Analyze News (Enter Text)");
        System.out.println("  2. Analyze News (From File)");
        System.out.println("  3. View Analysis History");
        System.out.println("  4. Search Analysis History");
        System.out.println("  5. Delete History Record");
        System.out.println("  6. Clear History");
        System.out.println("  7. About System");
        System.out.println("  8. Exit");
        System.out.println();
        System.out.print("  Enter your choice (1-8): ");
    }

    private void printSectionHeader(String title) {
        System.out.println();
        System.out.println(LINE_DOUBLE);
        System.out.println("  " + title);
        System.out.println(LINE_DOUBLE);
        System.out.println();
    }

    private void printError(String message) {
        System.out.println();
        System.out.println("  [ERROR] " + message);
        System.out.println();
    }

    private void showGoodbye() {
        System.out.println();
        System.out.println(LINE_DOUBLE);
        System.out.println("  Thank you for using TruthLens.");
        System.out.println("  Stay informed. Verify before you share.");
        System.out.println(LINE_DOUBLE);
        System.out.println();
    }

    /** Indent every line of a multi-line string. */
    private String indent(String text, String prefix) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (String line : text.split("\n")) {
            sb.append(prefix).append(line).append("\n");
        }
        return sb.toString().stripTrailing();
    }
}

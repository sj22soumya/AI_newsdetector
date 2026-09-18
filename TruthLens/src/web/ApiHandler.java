package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.*;
import service.*;
import exception.*;
import util.InputValidator;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Handles all HTTP API requests for the TruthLens web interface.
 * Routes:
 *   GET  /             → index.html
 *   GET  /api/history  → all records as JSON
 *   GET  /api/history/search?q=keyword → filtered records
 *   POST /api/analyze  → analyze article, return result JSON
 *   DELETE /api/history/{id} → delete record
 *   POST /api/history/clear  → clear all
 */
public class ApiHandler implements HttpHandler {

    private final NewsAnalysisService analysisService;
    private final HistoryService      historyService;
    private final String              webRoot; // path to web/ directory

    public ApiHandler(NewsAnalysisService analysisService,
                      HistoryService historyService,
                      String webRoot) {
        this.analysisService = analysisService;
        this.historyService  = historyService;
        this.webRoot         = webRoot;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Enable CORS for all responses
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 204, "");
            return;
        }

        String path   = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod().toUpperCase();

        try {
            if (path.equals("/") || path.equals("/index.html")) {
                serveHtml(exchange);
            } else if (path.equals("/api/analyze") && method.equals("POST")) {
                handleAnalyze(exchange);
            } else if (path.equals("/api/history") && method.equals("GET")) {
                handleGetHistory(exchange);
            } else if (path.startsWith("/api/history/search") && method.equals("GET")) {
                handleSearchHistory(exchange);
            } else if (path.startsWith("/api/history/") && method.equals("DELETE")) {
                handleDeleteRecord(exchange, path);
            } else if (path.equals("/api/history/clear") && method.equals("POST")) {
                handleClearHistory(exchange);
            } else {
                sendJson(exchange, 404, JsonBuilder.error("Not found: " + path));
            }
        } catch (Exception e) {
            sendJson(exchange, 500, JsonBuilder.error("Server error: " + e.getMessage()));
        }
    }

    // ---- Serve HTML file --------------------------------------------------

    private void serveHtml(HttpExchange exchange) throws IOException {
        String htmlPath = webRoot + File.separator + "index.html";
        File htmlFile = new File(htmlPath);
        byte[] bytes;
        if (htmlFile.exists()) {
            bytes = Files.readAllBytes(Paths.get(htmlPath));
        } else {
            bytes = "<h1>index.html not found</h1>".getBytes(StandardCharsets.UTF_8);
        }
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // ---- POST /api/analyze ------------------------------------------------

    private void handleAnalyze(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, String> params = parseJsonBody(body);

        String headline = params.getOrDefault("headline", "").trim();
        String content  = params.getOrDefault("content", "").trim();

        try {
            InputValidator.validateHeadline(headline);
            InputValidator.validateContent(content);

            NewsArticle article = new NewsArticle(headline, content);
            PredictionResult result = analysisService.analyze(article);
            Map<String, Number> stats = analysisService.getLastStats(article);

            String json = buildResultJson(result, stats);
            sendJson(exchange, 200, json);

        } catch (InvalidNewsException e) {
            sendJson(exchange, 400, JsonBuilder.error(e.getMessage()));
        } catch (Exception e) {
            sendJson(exchange, 500, JsonBuilder.error("Analysis failed: " + e.getMessage()));
        }
    }

    // ---- GET /api/history -------------------------------------------------

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<AnalysisRecord> records = historyService.getAllRecords();
        sendJson(exchange, 200, buildHistoryJson(records));
    }

    // ---- GET /api/history/search?q=keyword --------------------------------

    private void handleSearchHistory(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery(); // e.g. "q=health"
        String keyword = "";
        if (query != null && query.startsWith("q=")) {
            keyword = java.net.URLDecoder.decode(query.substring(2), StandardCharsets.UTF_8.name());
        }
        if (keyword.isEmpty()) {
            sendJson(exchange, 400, JsonBuilder.error("Search keyword cannot be empty."));
            return;
        }
        List<AnalysisRecord> results = historyService.searchByHeadline(keyword);
        sendJson(exchange, 200, buildHistoryJson(results));
    }

    // ---- DELETE /api/history/{id} -----------------------------------------

    private void handleDeleteRecord(HttpExchange exchange, String path) throws IOException {
        // path = /api/history/TL-XXXXXXXX
        String id = path.replace("/api/history/", "").trim();
        if (id.isEmpty()) {
            sendJson(exchange, 400, JsonBuilder.error("Record ID required."));
            return;
        }
        boolean deleted = historyService.deleteRecord(id);
        if (deleted) {
            sendJson(exchange, 200, JsonBuilder.success("Record " + id + " deleted."));
        } else {
            sendJson(exchange, 404, JsonBuilder.error("Record not found: " + id));
        }
    }

    // ---- POST /api/history/clear ------------------------------------------

    private void handleClearHistory(HttpExchange exchange) throws IOException {
        historyService.clearHistory();
        sendJson(exchange, 200, JsonBuilder.success("History cleared."));
    }

    // ---- JSON builders ----------------------------------------------------

    private String buildResultJson(PredictionResult result, Map<String, Number> stats) {
        String signalsJson = JsonBuilder.listToJsonArray(result.getDetectedSignals());
        return new JsonBuilder()
            .add("success", true)
            .add("riskScore",      result.getRiskScore())
            .add("classification", result.getClassification())
            .add("confidence",     result.getConfidencePercent())
            .add("explanation",    result.getExplanation())
            .add("recommendation", result.getRecommendation())
            .addRaw("signals",     signalsJson)
            .add("wordCount",      stats.get("wordCount").intValue())
            .add("sentenceCount",  stats.get("sentenceCount").intValue())
            .add("charCount",      stats.get("charCount").intValue())
            .add("uppercaseWordCount", stats.get("uppercaseWordCount").intValue())
            .add("uppercaseRatio", stats.get("uppercaseRatio").doubleValue())
            .add("exclamationCount", stats.get("exclamationCount").intValue())
            .add("questionMarkCount", stats.get("questionMarkCount").intValue())
            .add("urlCount",       stats.get("urlCount").intValue())
            .build();
    }

    private String buildHistoryJson(List<AnalysisRecord> records) {
        StringBuilder sb = new StringBuilder("{\"success\":true,\"count\":");
        sb.append(records.size()).append(",\"records\":[");
        for (int i = 0; i < records.size(); i++) {
            if (i > 0) sb.append(",");
            AnalysisRecord r = records.get(i);
            sb.append(new JsonBuilder()
                .add("id",             r.getId())
                .add("timestamp",      r.getFormattedTimestamp())
                .add("headline",       r.getHeadline())
                .add("riskScore",      r.getRiskScore())
                .add("classification", r.getClassification())
                .build());
        }
        sb.append("]}");
        return sb.toString();
    }

    // ---- HTTP helpers -----------------------------------------------------

    private void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Extremely simple JSON body parser for {"key":"value"} objects.
     * Handles string values only (sufficient for our API).
     */
    private Map<String, String> parseJsonBody(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.isBlank()) return map;
        // Strip outer braces
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}"))   json = json.substring(0, json.length() - 1);

        // Split by top-level commas (simple approach — handles our two-field body)
        // Tokenise key:value pairs
        int i = 0;
        while (i < json.length()) {
            // find next "key"
            int ks = json.indexOf('"', i);
            if (ks < 0) break;
            int ke = json.indexOf('"', ks + 1);
            if (ke < 0) break;
            String key = json.substring(ks + 1, ke);
            // find colon
            int colon = json.indexOf(':', ke);
            if (colon < 0) break;
            // find value (may be quoted string or unquoted)
            int vs = colon + 1;
            while (vs < json.length() && json.charAt(vs) == ' ') vs++;
            String value;
            if (vs < json.length() && json.charAt(vs) == '"') {
                // String value — find closing quote, respecting escapes
                StringBuilder val = new StringBuilder();
                int ci = vs + 1;
                while (ci < json.length()) {
                    char c = json.charAt(ci);
                    if (c == '\\' && ci + 1 < json.length()) {
                        char next = json.charAt(ci + 1);
                        if (next == 'n') val.append('\n');
                        else if (next == 'r') val.append('\r');
                        else if (next == 't') val.append('\t');
                        else val.append(next);
                        ci += 2;
                    } else if (c == '"') {
                        i = ci + 1;
                        break;
                    } else {
                        val.append(c);
                        ci++;
                    }
                }
                value = val.toString();
            } else {
                // Non-string value
                int ve = json.indexOf(',', vs);
                if (ve < 0) ve = json.length();
                value = json.substring(vs, ve).trim();
                i = ve + 1;
            }
            map.put(key, value);
        }
        return map;
    }
}

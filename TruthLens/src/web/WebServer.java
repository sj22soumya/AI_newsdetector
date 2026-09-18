package web;

import com.sun.net.httpserver.HttpServer;
import service.HistoryService;
import service.NewsAnalysisService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Embedded HTTP server for TruthLens web interface.
 * Uses Java's built-in com.sun.net.httpserver — no external dependencies.
 */
public class WebServer {

    private final int    port;
    private final String webRoot;
    private HttpServer   server;

    public WebServer(int port, String webRoot) {
        this.port    = port;
        this.webRoot = webRoot;
    }

    /** Start the server. Blocks until stop() is called. */
    public void start() throws IOException {
        HistoryService      historyService  = new HistoryService();
        NewsAnalysisService analysisService = new NewsAnalysisService(historyService);

        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Mount all routes on a single handler
        ApiHandler handler = new ApiHandler(analysisService, historyService, webRoot);
        server.createContext("/", handler);

        // Use a thread pool so multiple requests don't block each other
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("==================================================");
        System.out.println("  TruthLens Web Server started!");
        System.out.println("  Open your browser at: http://localhost:" + port);
        System.out.println("  Press Ctrl+C to stop the server.");
        System.out.println("==================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(1);
            System.out.println("Server stopped.");
        }
    }
}

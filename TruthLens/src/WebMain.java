import web.WebServer;

/**
 * Web entry point for TruthLens.
 * Starts the embedded HTTP server and opens the browser UI on localhost:8080.
 *
 * Run with:
 *   java -cp out WebMain
 */
public class WebMain {

    private static final int    PORT     = 8080;
    private static final String WEB_ROOT = "web"; // relative to project root

    public static void main(String[] args) throws Exception {
        WebServer server = new WebServer(PORT, WEB_ROOT);

        // Shutdown hook — stop server cleanly on Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
        }));

        server.start();

        // Try to open the browser automatically
        tryOpenBrowser("http://localhost:" + PORT);

        // Keep main thread alive
        Thread.currentThread().join();
    }

    private static void tryOpenBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                pb = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
            } else if (os.contains("mac")) {
                pb = new ProcessBuilder("open", url);
            } else {
                pb = new ProcessBuilder("xdg-open", url);
            }
            pb.start();
        } catch (Exception ignored) {
            // Browser open is best-effort; user can open manually
        }
    }
}

package oceanviewresort.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * AppServer - HTTP Server (no frameworks).
 *
 * Serves the Web UI and REST API on port 8080.
 * index.html is bundled inside the JAR (src/web/index.html)
 * so it works from any working directory.
 *
 * Open browser: http://localhost:8080
 */
public class AppServer {

    public static final int    PORT = 8080;
    public static final String BASE = "http://localhost:" + PORT;

    private HttpServer server;

    public void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Serve index.html for browser
        server.createContext("/",                 new WebHandler());

        // REST API endpoints
        server.createContext("/api/auth/login",   new UserHandler());
        server.createContext("/api/rooms",         new RoomHandler());
        server.createContext("/api/reservations",  new ReservationHandler());

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("===========================================");
        System.out.println("  Ocean View Resort  -  Web Application   ");
        System.out.println("===========================================");
        System.out.println("  Open browser: http://localhost:8080      ");
        System.out.println("===========================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server stopped.");
        }
    }

    // Serves index.html to the browser
    static class WebHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange ex) throws IOException {
            String path   = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();

            // CORS headers - required for browser fetch() calls
            ex.getResponseHeaders().set("Access-Control-Allow-Origin",  "*");
            ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
            ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Accept");

            if (method.equalsIgnoreCase("OPTIONS")) {
                ex.sendResponseHeaders(200, -1);
                return;
            }

            // Let REST handlers deal with API calls
            if (path.startsWith("/api/")) return;

            byte[] html = loadHtml();
            ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            ex.sendResponseHeaders(200, html.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(html);
            }
        }

        private byte[] loadHtml() {
            // 1ST: Load from inside the JAR (classpath) - always works
            try (InputStream is = AppServer.class.getResourceAsStream("/web/index.html")) {
                if (is != null) {
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    byte[] chunk = new byte[4096];
                    int n;
                    while ((n = is.read(chunk)) != -1) buf.write(chunk, 0, n);
                    System.out.println("Serving index.html from JAR classpath.");
                    return buf.toByteArray();
                }
            } catch (IOException ignored) {}

            // 2ND: Fallback - look on filesystem relative to working dir
            String[] paths = { "web/index.html", "src/web/index.html", "index.html" };
            for (String p : paths) {
                File f = new File(p);
                if (f.exists()) {
                    try {
                        System.out.println("Serving index.html from: " + f.getAbsolutePath());
                        return Files.readAllBytes(f.toPath());
                    } catch (IOException ignored) {}
                }
            }

            // Last resort - show helpful error page
            System.out.println("WARNING: index.html not found! Working dir: " + new File(".").getAbsolutePath());
            String err =
                "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Ocean View Resort</title>"
                + "<style>body{background:#004d4d;color:#f5d96b;font-family:sans-serif;"
                + "display:flex;align-items:center;justify-content:center;height:100vh;margin:0;text-align:center}"
                + "h1{font-size:2rem}p{color:white;margin:10px 0}code{background:rgba(0,0,0,.3);padding:4px 10px;border-radius:4px}</style>"
                + "</head><body><div>"
                + "<h1>Ocean View Resort</h1>"
                + "<p>index.html not found. Clean and rebuild the project in NetBeans.</p>"
                + "<p><code>Run: Clean and Build (Shift+F11), then Run (F6)</code></p>"
                + "</div></body></html>";
            return err.getBytes(StandardCharsets.UTF_8);
        }
    }
}

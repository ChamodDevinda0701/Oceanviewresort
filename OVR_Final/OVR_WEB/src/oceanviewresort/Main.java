package oceanviewresort;

import oceanviewresort.db.DatabaseConnection;
import oceanviewresort.server.AppServer;

/**
 * Main - Web Application Entry Point.
 *
 * 1. Connects to SQL Server (Singleton Pattern)
 * 2. Starts REST API + serves index.html on port 8080
 *
 * Open browser: http://localhost:8080
 * NO Swing UI - everything runs in the browser.
 */
public class Main {

    public static void main(String[] args) {

        // Step 1: Database - Singleton Pattern
        System.out.println("Connecting to SQL Server...");
        DatabaseConnection.getInstance();

        // Step 2: Start web server (UI + REST API)
        AppServer server = new AppServer();
        try {
            server.start();
        } catch (Exception e) {
            System.err.println("ERROR: Cannot start server on port 8080.");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        // Step 3: Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            DatabaseConnection.getInstance().closeConnection();
            System.out.println("Shutdown complete.");
        }));

        System.out.println("Press Ctrl+C to stop.");
    }
}

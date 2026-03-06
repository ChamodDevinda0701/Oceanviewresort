package oceanviewresort.client;

import oceanviewresort.server.AppServer;
import oceanviewresort.server.JsonUtil;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * ApiClient - HTTP client used by the Swing GUI.
 * All communication goes through REST API calls to the backend.
 * No direct database access from the UI layer.
 */
public class ApiClient {

    private static final String BASE = AppServer.BASE;

    // ── Authentication ────────────────────────────────────────

    /**
     * Login - POST /api/auth/login
     * Returns the JSON response or null on failure.
     */
    public static String login(String username, String password) {
        String body = "{\"username\":" +
            JsonUtil.string(username) + ",\"password\":" +
            JsonUtil.string(password) + "}";
        return post("/api/auth/login", body);
    }

    // ── Rooms ─────────────────────────────────────────────────

    /**
     * Get all rooms - GET /api/rooms
     */
    public static String getAllRooms() {
        return get("/api/rooms");
    }

    /**
     * Get available rooms only - GET /api/rooms/available
     */
    public static String getAvailableRooms() {
        return get("/api/rooms/available");
    }

    /**
     * Get single room - GET /api/rooms/{number}
     */
    public static String getRoom(String roomNumber) {
        return get("/api/rooms/" + roomNumber);
    }

    // ── Reservations ──────────────────────────────────────────

    /**
     * Get all reservations - GET /api/reservations
     */
    public static String getAllReservations() {
        return get("/api/reservations");
    }

    /**
     * Get one reservation - GET /api/reservations/{number}
     */
    public static String getReservation(String resNumber) {
        return get("/api/reservations/" + resNumber);
    }

    /**
     * Create reservation - POST /api/reservations
     */
    public static String createReservation(
            String guestName, String address, String contact,
            String roomNumber, String roomType,
            String checkIn, String checkOut) {

        String body = "{"
            + "\"guestName\":"     + JsonUtil.string(guestName)  + ","
            + "\"address\":"       + JsonUtil.string(address)    + ","
            + "\"contactNumber\":" + JsonUtil.string(contact)    + ","
            + "\"roomNumber\":"    + JsonUtil.string(roomNumber) + ","
            + "\"roomType\":"      + JsonUtil.string(roomType)   + ","
            + "\"checkInDate\":"   + JsonUtil.string(checkIn)    + ","
            + "\"checkOutDate\":"  + JsonUtil.string(checkOut)
            + "}";
        return post("/api/reservations", body);
    }

    /**
     * Cancel reservation - DELETE /api/reservations/{number}
     */
    public static String cancelReservation(String resNumber) {
        return delete("/api/reservations/" + resNumber);
    }

    // ── Core HTTP methods ─────────────────────────────────────

    private static String get(String endpoint) {
        try {
            URL url = new URL(BASE + endpoint);
            HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            return readResponse(conn);
        } catch (Exception e) {
            return JsonUtil.error("Connection failed: " +
                e.getMessage());
        }
    }

    private static String post(String endpoint, String jsonBody) {
        try {
            URL url = new URL(BASE + endpoint);
            HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(input);
            }
            return readResponse(conn);
        } catch (Exception e) {
            return JsonUtil.error("Connection failed: " +
                e.getMessage());
        }
    }

    private static String delete(String endpoint) {
        try {
            URL url = new URL(BASE + endpoint);
            HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            return readResponse(conn);
        } catch (Exception e) {
            return JsonUtil.error("Connection failed: " +
                e.getMessage());
        }
    }

    private static String readResponse(HttpURLConnection conn)
            throws IOException {
        int code = conn.getResponseCode();
        InputStream is = (code >= 200 && code < 300)
            ? conn.getInputStream()
            : conn.getErrorStream();

        if (is == null) return JsonUtil.error("No response");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    // ── Utility: check if status is success ───────────────────
    public static boolean isSuccess(String jsonResponse) {
        if (jsonResponse == null) return false;
        String status = JsonUtil.parseField(jsonResponse, "status");
        return "success".equals(status);
    }

    // ── Utility: extract error message ────────────────────────
    public static String getError(String jsonResponse) {
        if (jsonResponse == null)
            return "No response from server";
        String msg = JsonUtil.parseField(jsonResponse, "message");
        return msg != null ? msg : "Unknown error";
    }
}

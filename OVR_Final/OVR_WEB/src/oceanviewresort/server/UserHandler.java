package oceanviewresort.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import oceanviewresort.dao.UserDAO;
import oceanviewresort.model.User;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UserHandler - handles POST /api/auth/login
 *
 * Request  (JSON): { "username": "admin", "password": "admin123" }
 * Response (JSON): { "status": "success", "userId": "1",
 *                    "fullName": "Administrator" }
 */
public class UserHandler extends BaseHandler implements HttpHandler {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        // Handle CORS preflight
        if (method.equalsIgnoreCase("OPTIONS")) {
            sendResponse(exchange, 200, "{}");
            return;
        }

        if (method.equalsIgnoreCase("POST")) {
            handleLogin(exchange);
        } else {
            sendResponse(exchange, 405,
                JsonUtil.error("Method not allowed"));
        }
    }

    // POST /api/auth/login
    private void handleLogin(HttpExchange exchange)
            throws IOException {
        try {
            String body = readBody(exchange);
            String username = JsonUtil.parseField(body, "username");
            String password = JsonUtil.parseField(body, "password");

            if (username == null || password == null ||
                username.isEmpty() || password.isEmpty()) {
                sendResponse(exchange, 400,
                    JsonUtil.error("Username and password required"));
                return;
            }

            User user = userDAO.authenticate(username, password);
            if (user != null) {
                Map<String, String> fields = new LinkedHashMap<>();
                fields.put("status", "success");
                fields.put("message", "Login successful");
                fields.put("userId", String.valueOf(user.getId()));
                fields.put("username", user.getUsername());
                fields.put("fullName", user.getFullName());
                sendResponse(exchange, 200,
                    JsonUtil.object(fields));
            } else {
                sendResponse(exchange, 401,
                    JsonUtil.error("Invalid username or password"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500,
                JsonUtil.error("Server error: " + e.getMessage()));
        }
    }
}

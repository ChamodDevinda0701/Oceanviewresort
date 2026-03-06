package oceanviewresort.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import oceanviewresort.dao.RoomDAO;
import oceanviewresort.model.Room;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * RoomHandler - handles /api/rooms
 *
 * GET  /api/rooms              → all rooms
 * GET  /api/rooms/available    → available rooms only
 * GET  /api/rooms/{number}     → single room by number
 */
public class RoomHandler extends BaseHandler implements HttpHandler {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();

        if (method.equalsIgnoreCase("OPTIONS")) {
            sendResponse(exchange, 200, "{}");
            return;
        }

        if (!method.equalsIgnoreCase("GET")) {
            sendResponse(exchange, 405,
                JsonUtil.error("Method not allowed"));
            return;
        }

        try {
            if (path.equals("/api/rooms/available")) {
                handleGetAvailable(exchange);
            } else if (path.equals("/api/rooms")) {
                handleGetAll(exchange);
            } else {
                // /api/rooms/{roomNumber}
                String roomNum = getPathParam(exchange, "/api/rooms/");
                if (roomNum != null) {
                    handleGetOne(exchange, roomNum);
                } else {
                    sendResponse(exchange, 404,
                        JsonUtil.error("Endpoint not found"));
                }
            }
        } catch (Exception e) {
            sendResponse(exchange, 500,
                JsonUtil.error("Server error: " + e.getMessage()));
        }
    }

    // GET /api/rooms
    private void handleGetAll(HttpExchange exchange)
            throws IOException {
        List<Room> rooms = roomDAO.getAllRooms();
        sendResponse(exchange, 200, buildRoomArray(rooms));
    }

    // GET /api/rooms/available
    private void handleGetAvailable(HttpExchange exchange)
            throws IOException {
        List<Room> rooms = roomDAO.getAvailableRooms();
        sendResponse(exchange, 200, buildRoomArray(rooms));
    }

    // GET /api/rooms/{number}
    private void handleGetOne(HttpExchange exchange,
                              String roomNumber) throws IOException {
        Room room = roomDAO.getRoomByNumber(roomNumber);
        if (room == null) {
            sendResponse(exchange, 404,
                JsonUtil.error("Room not found: " + roomNumber));
        } else {
            sendResponse(exchange, 200, roomToJson(room));
        }
    }

    // ── Helpers ───────────────────────────────────────────────
    private String buildRoomArray(List<Room> rooms) {
        List<String> items = new ArrayList<>();
        for (Room r : rooms) items.add(roomToJson(r));
        return JsonUtil.array(items);
    }

    private String roomToJson(Room r) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("roomNumber",    r.getRoomNumber());
        m.put("roomType",      r.getRoomType());
        m.put("pricePerNight",
            String.valueOf(r.getPricePerNight()));
        m.put("isAvailable",
            String.valueOf(r.isAvailable()));
        return JsonUtil.object(m);
    }
}

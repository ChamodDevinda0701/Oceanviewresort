package oceanviewresort.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import oceanviewresort.dao.ReservationDAO;
import oceanviewresort.dao.RoomDAO;
import oceanviewresort.factory.RoomFactory;
import oceanviewresort.model.Reservation;
import oceanviewresort.observer.NotificationService;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ReservationHandler - handles /api/reservations
 *
 * GET    /api/reservations          → all reservations
 * GET    /api/reservations/{number} → single reservation
 * POST   /api/reservations          → create new reservation
 * DELETE /api/reservations/{number} → cancel reservation
 */
public class ReservationHandler extends BaseHandler
        implements HttpHandler {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO        roomDAO        = new RoomDAO();
    private final SimpleDateFormat sdf =
        new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();

        if (method.equalsIgnoreCase("OPTIONS")) {
            sendResponse(exchange, 200, "{}");
            return;
        }

        try {
            switch (method.toUpperCase()) {
                case "GET":
                    if (path.equals("/api/reservations")) {
                        handleGetAll(exchange);
                    } else {
                        String num = getPathParam(
                            exchange, "/api/reservations/");
                        if (num != null) handleGetOne(exchange, num);
                        else sendResponse(exchange, 404,
                            JsonUtil.error("Not found"));
                    }
                    break;

                case "POST":
                    handleCreate(exchange);
                    break;

                case "DELETE":
                    String num = getPathParam(
                        exchange, "/api/reservations/");
                    if (num != null) handleDelete(exchange, num);
                    else sendResponse(exchange, 400,
                        JsonUtil.error("Reservation number required"));
                    break;

                default:
                    sendResponse(exchange, 405,
                        JsonUtil.error("Method not allowed"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500,
                JsonUtil.error("Server error: " + e.getMessage()));
        }
    }

    // GET /api/reservations
    private void handleGetAll(HttpExchange exchange)
            throws IOException {
        List<Reservation> list = reservationDAO.getAllReservations();
        List<String> items = new ArrayList<>();
        for (Reservation r : list) items.add(reservationToJson(r));
        sendResponse(exchange, 200, JsonUtil.array(items));
    }

    // GET /api/reservations/{number}
    private void handleGetOne(HttpExchange exchange, String number)
            throws IOException {
        Reservation r = reservationDAO
            .getReservationByNumber(number);
        if (r == null) {
            sendResponse(exchange, 404,
                JsonUtil.error("Reservation not found: " + number));
        } else {
            sendResponse(exchange, 200, reservationToJson(r));
        }
    }

    // POST /api/reservations
    private void handleCreate(HttpExchange exchange)
            throws IOException {
        String body = readBody(exchange);

        String guestName  = JsonUtil.parseField(body, "guestName");
        String address    = JsonUtil.parseField(body, "address");
        String contact    = JsonUtil.parseField(body, "contactNumber");
        String roomNumber = JsonUtil.parseField(body, "roomNumber");
        String roomType   = JsonUtil.parseField(body, "roomType");
        String checkIn    = JsonUtil.parseField(body, "checkInDate");
        String checkOut   = JsonUtil.parseField(body, "checkOutDate");

        // Validate required fields
        if (guestName == null || address == null ||
            contact == null   || roomNumber == null ||
            roomType == null  || checkIn == null ||
            checkOut == null) {
            sendResponse(exchange, 400,
                JsonUtil.error("All fields are required"));
            return;
        }

        // Validate contact number
        if (!contact.matches("\\d{10}")) {
            sendResponse(exchange, 400,
                JsonUtil.error(
                    "Contact number must be exactly 10 digits"));
            return;
        }

        try {
            Date ciDate = sdf.parse(checkIn);
            Date coDate = sdf.parse(checkOut);

            if (!coDate.after(ciDate)) {
                sendResponse(exchange, 400,
                    JsonUtil.error(
                        "Check-out must be after check-in date"));
                return;
            }

            long nights = (coDate.getTime() - ciDate.getTime()) /
                          (1000 * 60 * 60 * 24);

            // Use Factory Pattern to get price
            double pricePerNight =
                RoomFactory.getPriceForType(roomType);
            if (pricePerNight == 0) {
                sendResponse(exchange, 400,
                    JsonUtil.error("Invalid room type: " + roomType));
                return;
            }
            double total = nights * pricePerNight;

            String resNum = "RES-" +
                (System.currentTimeMillis() % 100000);

            Reservation r = new Reservation(
                resNum, guestName, address, contact,
                roomNumber, roomType, ciDate, coDate, total);

            if (reservationDAO.addReservation(r)) {
                roomDAO.updateRoomAvailability(roomNumber, false);

                // Observer Pattern - notify listeners
                NotificationService.getInstance()
                    .notifyReservationAdded(r);

                Map<String, String> resp = new LinkedHashMap<>();
                resp.put("status", "success");
                resp.put("message", "Reservation created");
                resp.put("reservationNumber", resNum);
                resp.put("totalAmount", String.valueOf(total));
                sendResponse(exchange, 201, JsonUtil.object(resp));
            } else {
                sendResponse(exchange, 500,
                    JsonUtil.error("Failed to save reservation"));
            }

        } catch (Exception e) {
            sendResponse(exchange, 400,
                JsonUtil.error("Invalid data: " + e.getMessage()));
        }
    }

    // DELETE /api/reservations/{number}
    private void handleDelete(HttpExchange exchange, String number)
            throws IOException {
        Reservation r = reservationDAO
            .getReservationByNumber(number);
        if (r == null) {
            sendResponse(exchange, 404,
                JsonUtil.error("Reservation not found: " + number));
            return;
        }

        if (reservationDAO.deleteReservation(number)) {
            roomDAO.updateRoomAvailability(
                r.getRoomNumber(), true);
            NotificationService.getInstance()
                .notifyReservationCancelled(number);
            sendResponse(exchange, 200,
                JsonUtil.success("Reservation " + number +
                    " cancelled successfully"));
        } else {
            sendResponse(exchange, 500,
                JsonUtil.error("Failed to cancel reservation"));
        }
    }

    // ── Helper ────────────────────────────────────────────────
    private String reservationToJson(Reservation r) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("reservationNumber", r.getReservationNumber());
        m.put("guestName",         r.getGuestName());
        m.put("address",           r.getAddress());
        m.put("contactNumber",     r.getContactNumber());
        m.put("roomNumber",        r.getRoomNumber());
        m.put("roomType",          r.getRoomType());
        m.put("checkInDate",       sdf.format(r.getCheckInDate()));
        m.put("checkOutDate",      sdf.format(r.getCheckOutDate()));
        m.put("numberOfNights",
            String.valueOf(r.getNumberOfNights()));
        m.put("totalAmount",
            String.valueOf(r.getTotalAmount()));
        return JsonUtil.object(m);
    }
}

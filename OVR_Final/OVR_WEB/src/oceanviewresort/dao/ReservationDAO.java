package oceanviewresort.dao;

import oceanviewresort.db.DatabaseConnection;
import oceanviewresort.model.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ReservationDAO - Data Access Object for RESERVATIONS table.
 * Uses Microsoft SQL Server via JDBC.
 * BOOKING_DATE uses GETDATE() default in SQL Server.
 */
public class ReservationDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // Insert a new reservation
    public boolean addReservation(Reservation r) {
        String sql =
            "INSERT INTO RESERVATIONS (" +
            "  RESERVATION_NUMBER, GUEST_NAME, ADDRESS," +
            "  CONTACT_NUMBER, ROOM_NUMBER, ROOM_TYPE," +
            "  CHECK_IN_DATE, CHECK_OUT_DATE, TOTAL_AMOUNT" +
            ") VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps =
                getConn().prepareStatement(sql);
            ps.setString(1, r.getReservationNumber());
            ps.setString(2, r.getGuestName());
            ps.setString(3, r.getAddress());
            ps.setString(4, r.getContactNumber());
            ps.setString(5, r.getRoomNumber());
            ps.setString(6, r.getRoomType());
            // Use java.sql.Date for SQL Server DATE columns
            ps.setDate(7, new java.sql.Date(
                r.getCheckInDate().getTime()));
            ps.setDate(8, new java.sql.Date(
                r.getCheckOutDate().getTime()));
            ps.setDouble(9, r.getTotalAmount());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(
                "addReservation error: " + e.getMessage());
            return false;
        }
    }

    // Get a single reservation by number
    public Reservation getReservationByNumber(String resNumber) {
        String sql =
            "SELECT * FROM RESERVATIONS " +
            "WHERE RESERVATION_NUMBER = ?";
        try {
            PreparedStatement ps =
                getConn().prepareStatement(sql);
            ps.setString(1, resNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapReservation(rs);
        } catch (SQLException e) {
            System.err.println(
                "getReservation error: " + e.getMessage());
        }
        return null;
    }

    // Get all reservations ordered by booking date descending
    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        // SQL Server: ORDER BY BOOKING_DATE DESC
        String sql =
            "SELECT * FROM RESERVATIONS " +
            "ORDER BY BOOKING_DATE DESC";
        try {
            Statement stmt =
                getConn().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapReservation(rs));
            }
        } catch (SQLException e) {
            System.err.println(
                "getAllReservations error: " + e.getMessage());
        }
        return list;
    }

    // Delete (cancel) a reservation
    public boolean deleteReservation(String resNumber) {
        String sql =
            "DELETE FROM RESERVATIONS " +
            "WHERE RESERVATION_NUMBER = ?";
        try {
            PreparedStatement ps =
                getConn().prepareStatement(sql);
            ps.setString(1, resNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(
                "deleteReservation error: " + e.getMessage());
            return false;
        }
    }

    // Check if a reservation number already exists
    public boolean reservationExists(String resNumber) {
        String sql =
            "SELECT COUNT(*) FROM RESERVATIONS " +
            "WHERE RESERVATION_NUMBER = ?";
        try {
            PreparedStatement ps =
                getConn().prepareStatement(sql);
            ps.setString(1, resNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println(
                "reservationExists error: " + e.getMessage());
        }
        return false;
    }

    // Map a ResultSet row to a Reservation object
    private Reservation mapReservation(ResultSet rs)
            throws SQLException {
        return new Reservation(
            rs.getString("RESERVATION_NUMBER"),
            rs.getString("GUEST_NAME"),
            rs.getString("ADDRESS"),
            rs.getString("CONTACT_NUMBER"),
            rs.getString("ROOM_NUMBER"),
            rs.getString("ROOM_TYPE"),
            rs.getDate("CHECK_IN_DATE"),
            rs.getDate("CHECK_OUT_DATE"),
            rs.getDouble("TOTAL_AMOUNT")
        );
    }
}

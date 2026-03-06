package oceanviewresort.dao;

import oceanviewresort.db.DatabaseConnection;
import oceanviewresort.model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomDAO - Data Access Object for ROOMS table.
 * SQL Server uses BIT (1/0) for boolean fields.
 */
public class RoomDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // Get all rooms
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql =
            "SELECT * FROM ROOMS " +
            "ORDER BY ROOM_NUMBER";
        try {
            Statement stmt =
                getConn().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                rooms.add(mapRoom(rs));
            }
        } catch (SQLException e) {
            System.err.println(
                "getAllRooms error: " + e.getMessage());
        }
        return rooms;
    }

    // Get only available rooms (IS_AVAILABLE = 1 in SQL Server)
    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql =
            "SELECT * FROM ROOMS " +
            "WHERE IS_AVAILABLE = 1 " +
            "ORDER BY ROOM_TYPE";
        try {
            Statement stmt =
                getConn().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                rooms.add(mapRoom(rs));
            }
        } catch (SQLException e) {
            System.err.println(
                "getAvailableRooms error: " + e.getMessage());
        }
        return rooms;
    }

    // Update room availability (1 = available, 0 = occupied)
    public boolean updateRoomAvailability(String roomNumber,
                                          boolean isAvailable) {
        String sql =
            "UPDATE ROOMS SET IS_AVAILABLE = ? " +
            "WHERE ROOM_NUMBER = ?";
        try {
            PreparedStatement ps =
                getConn().prepareStatement(sql);
            // SQL Server BIT: true=1, false=0
            ps.setInt(1, isAvailable ? 1 : 0);
            ps.setString(2, roomNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(
                "updateRoomAvailability error: " +
                e.getMessage());
            return false;
        }
    }

    // Get a single room by room number
    public Room getRoomByNumber(String roomNumber) {
        String sql =
            "SELECT * FROM ROOMS " +
            "WHERE ROOM_NUMBER = ?";
        try {
            PreparedStatement ps =
                getConn().prepareStatement(sql);
            ps.setString(1, roomNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRoom(rs);
            }
        } catch (SQLException e) {
            System.err.println(
                "getRoomByNumber error: " + e.getMessage());
        }
        return null;
    }

    // Map ResultSet row to Room object
    // SQL Server BIT column: getBoolean() works correctly
    private Room mapRoom(ResultSet rs) throws SQLException {
        return new Room(
            rs.getString("ROOM_NUMBER"),
            rs.getString("ROOM_TYPE"),
            rs.getDouble("PRICE_PER_NIGHT"),
            rs.getBoolean("IS_AVAILABLE")
        );
    }
}

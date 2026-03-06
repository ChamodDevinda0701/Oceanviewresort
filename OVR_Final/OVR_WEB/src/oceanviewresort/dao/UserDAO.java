package oceanviewresort.dao;

import oceanviewresort.db.DatabaseConnection;
import oceanviewresort.model.User;
import java.sql.*;

/**
 * UserDAO - Data Access Object for USERS table.
 * Communicates with Microsoft SQL Server via JDBC.
 */
public class UserDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // Authenticate user - used by backend UserHandler
    public User authenticate(String username, String password) {
        String sql =
            "SELECT * FROM USERS " +
            "WHERE USERNAME = ? AND PASSWORD = ?";
        try {
            PreparedStatement ps =
                getConn().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("ID"),
                    rs.getString("USERNAME"),
                    rs.getString("PASSWORD"),
                    rs.getString("FULL_NAME")
                );
            }
        } catch (SQLException e) {
            System.err.println(
                "Auth error: " + e.getMessage());
        }
        return null;
    }

    // Add a new staff user
    public boolean addUser(User user) {
        String sql =
            "INSERT INTO USERS " +
            "(USERNAME, PASSWORD, FULL_NAME) " +
            "VALUES (?, ?, ?)";
        try {
            PreparedStatement ps =
                getConn().prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(
                "Add user error: " + e.getMessage());
            return false;
        }
    }
}

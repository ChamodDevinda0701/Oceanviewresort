package oceanviewresort.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseConnection - Singleton Pattern.
 *
 * Connects to Microsoft SQL Server using Windows Authentication.
 * Only ONE connection instance is ever created (Singleton).
 *
 * Connection details:
 *   Server   : localhost
 *   Database : OceanViewResortDB
 *   Auth     : Windows Authentication (integratedSecurity=true)
 */
public class DatabaseConnection {

    // ── Singleton instance ────────────────────────────────────
    private static DatabaseConnection instance;
    private Connection connection;

    // ── SQL Server connection URL ─────────────────────────────
    // Windows Authentication - no username/password needed
    private static final String DB_URL =
        "jdbc:sqlserver://localhost:1433;" +
        "databaseName=OceanViewResortDB;" +
        "integratedSecurity=true;" +
        "trustServerCertificate=true;";

    // ── Private constructor (Singleton) ───────────────────────
    private DatabaseConnection() {
        try {
            // Load Microsoft SQL Server JDBC driver
            Class.forName(
                "com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(DB_URL);
            System.out.println(
                "SQL Server connected successfully.");
            System.out.println(
                "Database: OceanViewResortDB @ localhost");
            initializeTables();
        } catch (ClassNotFoundException e) {
            System.err.println(
                "JDBC Driver not found. " +
                "Make sure mssql-jdbc.jar is in the lib/ folder.\n"
                + e.getMessage());
        } catch (SQLException e) {
            System.err.println(
                "SQL Server connection failed.\n" +
                "Make sure:\n" +
                "  1. SQL Server is running\n" +
                "  2. Database 'OceanViewResortDB' exists\n" +
                "  3. mssql-jdbc_auth DLL is in System32\n" +
                "Error: " + e.getMessage());
        }
    }

    // ── Get or create the single instance ─────────────────────
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    // ── Get connection (reconnect if closed) ──────────────────
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Reconnected to SQL Server.");
            }
        } catch (SQLException e) {
            System.err.println(
                "Reconnection failed: " + e.getMessage());
        }
        return connection;
    }

    // ── Create all tables on first run ────────────────────────
    private void initializeTables() {
        createUsersTable();
        createRoomsTable();
        createReservationsTable();
        insertDefaultUser();
        insertDefaultRooms();
    }

    private void createUsersTable() {
        String sql =
            "IF NOT EXISTS (" +
            "  SELECT * FROM sysobjects " +
            "  WHERE name='USERS' AND xtype='U'" +
            ") " +
            "CREATE TABLE USERS (" +
            "  ID       INT IDENTITY(1,1) PRIMARY KEY," +
            "  USERNAME VARCHAR(50)  NOT NULL UNIQUE," +
            "  PASSWORD VARCHAR(50)  NOT NULL," +
            "  FULL_NAME VARCHAR(100) NOT NULL" +
            ")";
        executeSQL(sql, "USERS table ready.");
    }

    private void createRoomsTable() {
        String sql =
            "IF NOT EXISTS (" +
            "  SELECT * FROM sysobjects " +
            "  WHERE name='ROOMS' AND xtype='U'" +
            ") " +
            "CREATE TABLE ROOMS (" +
            "  ROOM_NUMBER    VARCHAR(10)  PRIMARY KEY," +
            "  ROOM_TYPE      VARCHAR(30)  NOT NULL," +
            "  PRICE_PER_NIGHT FLOAT       NOT NULL," +
            "  IS_AVAILABLE   BIT          NOT NULL DEFAULT 1" +
            ")";
        executeSQL(sql, "ROOMS table ready.");
    }

    private void createReservationsTable() {
        String sql =
            "IF NOT EXISTS (" +
            "  SELECT * FROM sysobjects " +
            "  WHERE name='RESERVATIONS' AND xtype='U'" +
            ") " +
            "CREATE TABLE RESERVATIONS (" +
            "  RESERVATION_NUMBER VARCHAR(20)  PRIMARY KEY," +
            "  GUEST_NAME         VARCHAR(100) NOT NULL," +
            "  ADDRESS            VARCHAR(200) NOT NULL," +
            "  CONTACT_NUMBER     VARCHAR(15)  NOT NULL," +
            "  ROOM_NUMBER        VARCHAR(10)  NOT NULL," +
            "  ROOM_TYPE          VARCHAR(30)  NOT NULL," +
            "  CHECK_IN_DATE      DATE         NOT NULL," +
            "  CHECK_OUT_DATE     DATE         NOT NULL," +
            "  TOTAL_AMOUNT       FLOAT        NOT NULL," +
            "  BOOKING_DATE       DATETIME     DEFAULT GETDATE()" +
            ")";
        executeSQL(sql, "RESERVATIONS table ready.");
    }

    private void insertDefaultUser() {
        String sql =
            "IF NOT EXISTS (" +
            "  SELECT 1 FROM USERS WHERE USERNAME = 'admin'" +
            ") " +
            "INSERT INTO USERS (USERNAME, PASSWORD, FULL_NAME) " +
            "VALUES ('admin', 'admin123', 'Administrator')";
        executeSQL(sql, "Default admin user ready.");
    }

    private void insertDefaultRooms() {
        String[][] rooms = {
            {"101", "Standard", "5000.00"},
            {"102", "Standard", "5000.00"},
            {"201", "Deluxe",   "9500.00"},
            {"202", "Deluxe",   "9500.00"},
            {"301", "Suite",    "18000.00"},
            {"302", "Suite",    "18000.00"}
        };
        for (String[] r : rooms) {
            String sql =
                "IF NOT EXISTS (" +
                "  SELECT 1 FROM ROOMS " +
                "  WHERE ROOM_NUMBER = '" + r[0] + "'" +
                ") " +
                "INSERT INTO ROOMS " +
                "(ROOM_NUMBER, ROOM_TYPE, " +
                " PRICE_PER_NIGHT, IS_AVAILABLE) " +
                "VALUES ('" + r[0] + "','" + r[1] + "'," +
                r[2] + ", 1)";
            executeSQL(sql, null);
        }
        System.out.println("Default rooms ready.");
    }

    private void executeSQL(String sql, String successMsg) {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            if (successMsg != null)
                System.out.println(successMsg);
        } catch (SQLException e) {
            System.err.println(
                "SQL Error: " + e.getMessage());
        }
    }

    // ── Close connection on app exit ──────────────────────────
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(
                    "SQL Server connection closed.");
            }
        } catch (SQLException e) {
            System.err.println(
                "Error closing connection: " + e.getMessage());
        }
    }
}

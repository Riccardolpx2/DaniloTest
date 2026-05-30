package server.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:database.db";
    private static Connection conn;

    public static Connection getConnection() throws SQLException {//metodo statico per ottenere connessione
        if (conn == null || conn.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection(DB_URL);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static void closeConnection() throws SQLException {//idem per chiudere
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}

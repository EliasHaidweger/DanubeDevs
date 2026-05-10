package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Zentrale Verbindung zur SQL Server Datenbank.
 */
public class DatabaseConnection {

    // ====== HIER ANPASSEN ======
    private static final String SERVER   = "185.119.119.126";
    private static final String DATABASE = "DanubeDevs";
    private static final String USER     = "danube";
    private static final String PASSWORD = "danube";
    private static final int    PORT     = 1433;
    // ===========================

    private static final String URL =
            "jdbc:sqlserver://" + SERVER + ":" + PORT
            + ";databaseName=" + DATABASE
            + ";encrypt=true;trustServerCertificate=true";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

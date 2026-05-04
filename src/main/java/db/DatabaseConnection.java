package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // TODO:
    private static final String URL = "jdbc:sqlserver://185.119.119.126:1433;database=DanubeDevs;encrypt=true;trustServerCertificate=true";
    private static final String USER = "danube";
    private static final String PASSWORD = "danube";

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

package com.travel.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/weather_travel_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        if (url == null || url.isBlank()) {
            url = DEFAULT_URL;
        }
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        if (user == null || user.isBlank() || password == null || password.isBlank()) {
            throw new SQLException("DB_USER and DB_PASSWORD must be configured.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found.", e);
        }
        return DriverManager.getConnection(url, user, password);
    }
}
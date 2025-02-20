package com.tems.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.mariadb.jdbc.MariaDbPoolDataSource;

public class ConnectionManager {
    private static MariaDbPoolDataSource dataSource;

    static {
        try {
            String url = String.format("jdbc:mariadb://%s:%s/%s",
                    System.getenv("DB_HOST"), System.getenv("DB_PORT"), System.getenv("DB_NAME"));
            dataSource = new MariaDbPoolDataSource(url);
            dataSource.setUser(System.getenv("DB_USER"));
            dataSource.setPassword(System.getenv("DB_PASSWORD"));
            dataSource.setMaxPoolSize(10); 
        } catch (SQLException e) {
            throw new ExceptionInInitializerError("Database connection pool initialization failed: " + e.getMessage());
        }
    }

    // Get a connection from the pool
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Close pool (use during application shutdown)
    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}

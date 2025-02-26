package com.tems.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;

public class ConnectionManager {
    private static HikariDataSource dataSource;

    static {
        try {
            // Load environment variables from the .env file
            Dotenv dotenv = Dotenv.load();

            // Retrieve environment variables for configuration
            String host = dotenv.get("DB_HOST");
            String port = dotenv.get("DB_PORT");
            String dbName = dotenv.get("DB_NAME");
            String user = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            // Configure HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format("jdbc:mariadb://%s:%s/%s", host, port, dbName));
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(10);  // Set maximum pool size (can adjust as needed)
            config.setIdleTimeout(30000);  // 30 seconds
            config.setConnectionTimeout(30000);  // 30 seconds
            config.setMaxLifetime(600000);  // 10 minutes

            // Initialize HikariCP DataSource
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            // Handle initialization failure
            throw new ExceptionInInitializerError("Database connection pool initialization failed: " + e.getMessage());
        }
    }

    // Retrieve a connection from the pool
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Close the connection pool (should be used during application shutdown)
    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}

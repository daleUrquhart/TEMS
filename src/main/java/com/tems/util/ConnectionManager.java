package com.tems.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;

public class ConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
    private static HikariDataSource dataSource;

    static {
        try {
            // Load environment variables
            Dotenv dotenv = Dotenv.load();
            
            // Retrieve and validate environment variables
            String host = Objects.requireNonNull(dotenv.get("DB_HOST"), "DB_HOST is missing");
            String port = Objects.requireNonNull(dotenv.get("DB_PORT"), "DB_PORT is missing");
            String dbName = Objects.requireNonNull(dotenv.get("DB_NAME"), "DB_NAME is missing");
            String user = Objects.requireNonNull(dotenv.get("DB_USER"), "DB_USER is missing");
            String password = Objects.requireNonNull(dotenv.get("DB_PASSWORD"), "DB_PASSWORD is missing");

            // Configure HikariCP for a remote connection
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format("jdbc:mariadb://%s:%s/%s", host, port, dbName));
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(10);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(600000);

            // Add some network-specific settings for better handling
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            
            // Initialize DataSource
            dataSource = new HikariDataSource(config);
            LOGGER.info("Database connection pool initialized successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database connection pool.", e);
            throw new ExceptionInInitializerError("Database connection pool initialization failed: " + e.getMessage());
        }
    }

    // Get a connection from the pool
    public static Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection.", e);
            throw e;
        }
    }

    // Close the connection pool during shutdown
    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
            LOGGER.info("Database connection pool closed.");
        }
    }
}

package com.tems;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.tems.util.ConnectionManager;

public class ConnectionManagerTest {

    @Test
    public void testGetConnectionDoesNotThrow() {
        // Test that getting a connection does not throw any exceptions
        assertDoesNotThrow(() -> {
            Connection connection = ConnectionManager.getConnection(); 
            assertNotNull(connection, "Expected a valid connection but got null"); 
            System.out.println("Successfully obtained a connection");  
            connection.close(); // Close the connection after use
        });
    } 

    @Test
    public void testConnectionPool() {
        // Test that obtaining multiple connections from the pool works as expected
        assertDoesNotThrow(() -> {
            // Request two connections from the pool
            Connection conn1 = ConnectionManager.getConnection();
            Connection conn2 = ConnectionManager.getConnection();
            
            // Check that both connections are not null
            assertNotNull(conn1, "Expected a valid connection but got null");
            assertNotNull(conn2, "Expected a valid connection but got null");

            // Check that both connections are valid (even though they may be the same instance due to pooling)
            assertTrue(conn1.isValid(10), "Connection 1 is not valid");
            assertTrue(conn2.isValid(10), "Connection 2 is not valid");

            // Optionally, log successful connection acquisition
            System.out.println("Successfully obtained two connections from the pool");

            // Close the connections to return them to the pool
            conn1.close();
            conn2.close();
        });
    }

    @AfterAll
    public static void cleanup() { 
        ConnectionManager.closePool(); // Close the pool after all tests
        System.out.println("Completed ConnectionManager tests");
    }
}

package com.tems;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import com.tems.models.User;

public class UserTest {
    
    @AfterEach
    public void removeUsersCreated() { 
        User.deleteAllUsers();
    } 

    @Test
    public void createTest() {
        // Test creating a valid user, invalid role, 
        int u1 = User.create("John Doe", "doe@example.ca", "MyPassword", "auditionee");
        int u2 = User.create("John Doe", "doe@example.ca", "MyPassword", "invalid role"); 
        int u3 = User.create("Alice Winston", "doe@example.ca", "MyPassword", "recruiter");

        assertNotEquals(u1, -1);
        assertEquals(u2, -1);
        assertEquals(u3, -1);  
    }

    @Test
    public void getUserByEmailTest() {
        // Ensure test user is present
        User.create("John Doe", "doe@example.ca", "MyPassword", "auditionee");

        assertDoesNotThrow(() -> User.getUserByEmail("doe@example.ca"));
        assertThrows(SQLException.class, () -> User.getUserByEmail("dne@example.ca"));
    } 

    @Test
    public void updateUserTest() {
        // Create a user
        User.create("John Doe", "doe@example.ca", "MyPassword", "auditionee");
        //assertTrue(userCreated); 

        try {
            // Get the user by email (this will throw an exception if user is not found)
            User u1 = User.getUserByEmail("doe@example.ca");

            // Update user data
            u1.setName("James");
            u1.setEmail("james_doe@example.ca");
            u1.setPasswordHash("MyNewPassword");
            u1.setRole("recruiter");

            // Perform update operation
            boolean updated = u1.update();
            assertTrue(updated); 

            // Verify the updated user details
            User updatedUser = User.getUserByEmail("james_doe@example.ca");
            assertEquals(u1.toString(), updatedUser.toString());

        } catch (SQLException e) {
            System.err.println("Error during user update test: " + e.getMessage());
            fail("SQLException thrown during test: " + e.getMessage()); 
        }
    }

    @Test
    public void getUserByIdTest() {
        // Ensure test user is present
        User.create("John Doe", "doe@example.ca", "MyPassword", "auditionee");
        try {
            User u = User.getUserByEmail("doe@example.ca");
            assertDoesNotThrow(() -> User.getUserById(u.getUserId()));
            assertThrows(SQLException.class, () -> User.getUserById(-1));
        } catch(SQLException e) {
            System.err.println("Failed to get test user by id: " + e.getMessage());
            fail();
        }
        
    } 

    @Test
    public void deleteUserTest() {
        User.create("John Doe", "doe@example.ca", "MyPassword", "auditionee");
        try {
            User u = User.getUserByEmail("doe@example.ca");
            User.delete(u.getUserId());
        } catch(SQLException e) {
            System.err.println("Failed to get test user by id: " + e.getMessage());
            fail();
        }
    }
}

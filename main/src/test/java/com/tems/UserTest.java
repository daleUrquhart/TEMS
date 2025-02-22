package com.tems;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import com.tems.models.User;

public class UserTest {
    
    @Test
    public void createUserTest() {
        // Test creating a valid user, invalid role, 
        boolean u1 = User.createUser("John Doe", "doe@example.ca", "MyPassword", "auditionee");
        boolean u2 = User.createUser("John Doe", "doe@example.ca", "MyPassword", "invalid role"); 
        boolean u3 = User.createUser("Alice Winston", "doe@example.ca", "MyPassword", "recruiter");

        assertTrue(u1);
        assertFalse(u2);
        assertFalse(u3); 
    }

    @Test
    public void getUserByEmailTest() {
        // Ensure test user is present
        User.createUser("John Doe", "doe@example.ca", "MyPassword", "auditionee");

        assertDoesNotThrow(() -> User.getUserByEmail("doe@example.ca"));
        assertThrows(SQLException.class, () -> User.getUserByEmail("dne@example.ca"));
    } 

    @Test
    public void updateUserTest() {
        // Create a user
        boolean userCreated = User.createUser("John Doe", "doe@example.ca", "MyPassword", "auditionee");
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
            boolean updated = u1.updateUser();
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
        User.createUser("John Doe", "doe@example.ca", "MyPassword", "auditionee");
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
        User.createUser("John Doe", "doe@example.ca", "MyPassword", "auditionee");
        try {
            User u = User.getUserByEmail("doe@example.ca");
            User.deleteUser(u.getUserId());
        } catch(SQLException e) {
            System.err.println("Failed to get test user by id: " + e.getMessage());
            fail();
        }
    }
}

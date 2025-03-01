package com.tems;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import com.tems.models.User;
import com.tems.util.PasswordManager;
import com.tems.util.SampleData;

public class UserTest {
    
    /*
    @AfterEach
    public void removeUsersCreated() { 
        try{User.deleteAllUsers();}
        catch(SQLException e) {
            System.out.println("Error deleting test user data" + e.getMessage());
            fail();
        }
    } 
     */

    @Test
    public void loadData() {
        SampleData.load();
    }

    @Test
    public void createTest() {
        // Test creating a valid user, invalid role, 
        try{
            String hash = PasswordManager.hashPassword("a");
            User.create("John Doe", "doe@example.ca", hash, "auditionee");
            User.create("John Doe", "doe@example.ca", hash, "invalid role"); 
            User.create("Alice Winston", "doe@example.ca", hash, "recruiter");
        }catch(SQLException e) {
            System.out.println("Error creating test user data" + e.getMessage());
            fail();
        }
    }

    @Test
    public void getUserByEmailTest() {
        // Ensure test user is present
        assertDoesNotThrow(() -> User.create("John Doe", "doe@example.ca", "MyPassword", "auditionee"));

        assertDoesNotThrow(() -> User.getUserByEmail("doe@example.ca"));
        SQLException thrown = assertThrows(SQLException.class, () -> User.getUserByEmail("dne@example.ca"));
        assertEquals("No user found with email: dne@example.ca", thrown.getMessage());
    } 

    @Test
    public void updateUserTest() {
        // Create a user
        assertDoesNotThrow(() -> User.create("John Doe", "doe@example.ca", "MyPassword", "auditionee"));
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
            u1.update();

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
        assertDoesNotThrow(() -> User.create("John Doe", "doe@example.ca", "MyPassword", "auditionee"));
        try {
            User u = User.getUserByEmail("doe@example.ca");
            assertDoesNotThrow(() -> User.getById(u.getUserId()));
            
        } catch(SQLException e) {
            System.err.println("Failed to get test user by id: " + e.getMessage());
            fail();
        }
        SQLException thrown = assertThrows(SQLException.class, () -> User.getById(-1));
        assertEquals("No user found with id: -1", thrown.getMessage());
        
    } 

    @Test
    public void deleteUserTest() {
        assertDoesNotThrow(() -> User.create("John Doe", "doe@example.ca", "MyPassword", "auditionee"));
        try {
            User u = User.getUserByEmail("doe@example.ca");
            User.delete(u.getUserId());
        } catch(SQLException e) {
            System.err.println("Failed to get test user by id: " + e.getMessage());
            fail();
        }
    }
}

package com.tems;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import com.tems.models.Auditionee;
import com.tems.models.Gender;
import com.tems.models.User;

public class AuditioneeTest {
    
    @AfterEach
    public void removeUsersCreated() { 
        try{User.deleteAllUsers();}
        catch(SQLException e) {
            System.out.println("Error deleting test user data" + e.getMessage());
            fail();
        }
    } 

    @Test 
    public void createAuditioneeTest() {
        assertDoesNotThrow(() -> Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1));
    }
}

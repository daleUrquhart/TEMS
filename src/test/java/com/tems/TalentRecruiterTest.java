package com.tems;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

import com.tems.models.TalentRecruiter;
import com.tems.models.User;

public class TalentRecruiterTest {
    
    @AfterEach
    public void removeUsersCreated() { 
        User.deleteAllUsers();
    } 
    
    @Test
    public void createTRTest() {
        int status = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");
        assertNotEquals(status, -1);
    }

    @Test
    public void getByIdTest() {
        int status = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");
        assertNotEquals(status, -1);
        try {
            TalentRecruiter r = TalentRecruiter.getById(status);
            assertEquals("John from Company 1", r.toString());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
    }
}
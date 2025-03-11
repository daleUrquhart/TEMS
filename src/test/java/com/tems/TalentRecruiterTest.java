package com.tems;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import com.tems.models.TalentRecruiter;
import com.tems.models.User;

public class TalentRecruiterTest {
    
    @AfterEach
    public void removeUsersCreated() { 
        try{User.deleteAllUsers();}
        catch(SQLException e) {
            System.out.println("Error deleting test user data" + e.getMessage());
            fail();
        }
    } 
    
    @Test
    public void createTRTest() {
        try { 
            TalentRecruiter.create("Bob", "bob@recruiter.ca", "Pass", "Talent Co.");
        } catch(SQLException e) {
            System.out.println("Error creating test user data" + e.getMessage());
            fail();
        }
    }

    @Test
    public void getByIdTest() {
        int trId = -1;
        try { 
            trId = TalentRecruiter.create("Bob", "bob@recruiter.ca", "Pass", "Talent Co.");
        } catch(SQLException e) {
            System.out.println("Error creating test user data" + e.getMessage());
            fail();
        }
        try {
            TalentRecruiter r = TalentRecruiter.getById(trId);
            assertEquals("Bob from Talent Co.", r.toString());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
    }
}
package com.tems;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import com.tems.models.Application;
import com.tems.models.Auditionee;
import com.tems.models.CriteriaType;
import com.tems.models.Gender;
import com.tems.models.Genre;
import com.tems.models.Listing;
import com.tems.models.TalentRecruiter;
import com.tems.models.User;

public class ApplicationTest {
    
    @AfterEach
    public void removeUsersCreated() { 
        try{User.deleteAllUsers();}
        catch(SQLException e) {
            System.out.println("Error deleting test user data" + e.getMessage());
            fail();
        }
    } 
    
    @Test
    public void createTest() {
        // Create sample auditionee, talent recruiter, and listing
        try {
            // Define gender roles
            ArrayList<Gender> genderRoles = new ArrayList<>();
            genderRoles.add(Gender.MALE);
            genderRoles.add(Gender.FEMALE);

            // Define Criteria and Weights
            Map<CriteriaType, Integer> selectedCriteria = new HashMap<>();
            selectedCriteria.put(CriteriaType.PHYSICAL_APPEARANCE, 1);

            int aId = Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1, genderRoles);
            int trId = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");
            ArrayList<Gender> genders = new ArrayList<>();
            genders.add(Gender.MALE);
            ArrayList<Genre> genres = new ArrayList<>();
            genres.add(Genre.ACTION);    
            // Valid Listing
            int lId = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, selectedCriteria);
            
            assertNotEquals(-1, aId);
            assertNotEquals(-1, trId);
            assertNotEquals(-1, lId);

            String resume = "My Resume";
            String coverLetter = "My Cover Letter";
            Application.create(aId, lId, resume, coverLetter);
        } catch(SQLException e) {
            System.out.println("Error creating application"+e.getMessage());
            fail();
        }
    }

    @Test 
    public void getByAudTest() {
        // Create sample auditionee, talent recruiter, and listing 
        try {
            // Define gender roles
            ArrayList<Gender> genderRoles = new ArrayList<>();
            genderRoles.add(Gender.MALE);
            genderRoles.add(Gender.FEMALE);

            // Define Criteria and Weights
            Map<CriteriaType, Integer> selectedCriteria = new HashMap<>();
            selectedCriteria.put(CriteriaType.PHYSICAL_APPEARANCE, 1);

            int aId = Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1, genderRoles);
            int trId = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");
            
            ArrayList<Gender> genders = new ArrayList<>();
            genders.add(Gender.MALE);
            ArrayList<Genre> genres = new ArrayList<>();
            genres.add(Genre.ACTION);   
            // Valid Listing
            int lId = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, selectedCriteria);
            
            assertNotEquals(-1, aId);
            assertNotEquals(-1, trId);
            assertNotEquals(-1, lId);

            String resume = "My Resume";
            String coverLetter = "My Cover Letter";
            int appId = Application.create(aId, lId, resume, coverLetter);
            assertNotEquals(-1, appId);
            ArrayList<Application> listingApps = new ArrayList<>(Application.getByListingId(lId));
            assertEquals(Application.getById(appId).toString(), listingApps.get(0).toString()); 
        } catch(SQLException e) {
            System.out.println("Error creating test user data" + e.getMessage());
            fail();
        }
        
    }

    @Test 
    public void declineAndAcceptTest() {
        // Create sample auditionee, talent recruiter, and listing 
        try {
            // Define gender roles
            ArrayList<Gender> genderRoles = new ArrayList<>();
            genderRoles.add(Gender.MALE);
            genderRoles.add(Gender.FEMALE);

            // Define Criteria and Weights
            Map<CriteriaType, Integer> selectedCriteria = new HashMap<>();
            selectedCriteria.put(CriteriaType.PHYSICAL_APPEARANCE, 1);

            int aId = Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1, genderRoles);
            int trId = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");
            
            ArrayList<Gender> genders = new ArrayList<>();
            genders.add(Gender.MALE);
            ArrayList<Genre> genres = new ArrayList<>();
            genres.add(Genre.ACTION);   
            // Valid Listing
            int lId = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, selectedCriteria);
            
            assertNotEquals(-1, aId);
            assertNotEquals(-1, trId);
            assertNotEquals(-1, lId);

            String resume = "My Resume";
            String coverLetter = "My Cover Letter";
            int appId = Application.create(aId, lId, resume, coverLetter);
            assertNotEquals(-1, appId);
            Application.decline(aId, lId);
            Application app = Application.getById(appId);
            assertEquals("rejected", app.getStatus());
            Application.accept(aId, lId);
            app = Application.getById(appId);
            assertEquals("accepted", app.getStatus());
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            fail();
        }
        
    }

    @Test
    public void scoreTest() {
        // Create sample auditionee, talent recruiter, and listing 
        try {
            ArrayList<Gender> genderRoles = new ArrayList<>();
            genderRoles.add(Gender.MALE);
            genderRoles.add(Gender.FEMALE);
            Map<CriteriaType, Integer> selectedCriteria = new HashMap<>();
            selectedCriteria.put(CriteriaType.PHYSICAL_APPEARANCE, 1);
            int aId = Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1, genderRoles);
            int trId = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");
        
            ArrayList<Gender> genders = new ArrayList<>();
            genders.add(Gender.MALE);
            ArrayList<Genre> genres = new ArrayList<>();
            genres.add(Genre.ACTION);   
            // Valid Listing

            int lId = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, selectedCriteria);
            
            assertNotEquals(-1, aId);
            assertNotEquals(-1, trId);
            assertNotEquals(-1, lId);

            String resume = "My Resume";
            String coverLetter = "My Cover Letter";
            int appId = Application.create(aId, lId, resume, coverLetter);
            assertNotEquals(-1, appId); 
            Application app = Application.getById(appId);
            //Map<CriteriaType, Integer> c = Criteria.getByListingId(lId);
            //int[] s = new int[]{99};
            //app.setFinalScore(c.keySet(), s);
            assertEquals(99, app.getFinalScore());
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }
}

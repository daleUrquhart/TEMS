package com.tems;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

public class ApplicationTest {
    
    @Test
    public void createTest() {
        // Create sample auditionee, talent recruiter, and listing
        int aId = Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1);
        int trId = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");

        ArrayList<Gender> genders = new ArrayList<>();
        genders.add(Gender.MALE);
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(Genre.ACTION);  
        ArrayList<CriteriaType> criteriaTypes = new ArrayList<>();
        int[] weights = new int[]{1};
        criteriaTypes.add(CriteriaType.PHYSICAL_APPEARANCE);
        // Valid Listing
        int lId = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, criteriaTypes, weights);
        
        assertNotEquals(-1, aId);
        assertNotEquals(-1, trId);
        assertNotEquals(-1, lId);

        String resume = "My Resume";
        String coverLetter = "My Cover Letter";
        assertDoesNotThrow(() -> Application.create(aId, lId, resume, coverLetter));
    }

    @Test 
    public void getByAudTest() {
        // Create sample auditionee, talent recruiter, and listing
        int aId = Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1);
        int trId = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");

        ArrayList<Gender> genders = new ArrayList<>();
        genders.add(Gender.MALE);
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(Genre.ACTION);  
        ArrayList<CriteriaType> criteriaTypes = new ArrayList<>();
        int[] weights = new int[]{1};
        criteriaTypes.add(CriteriaType.PHYSICAL_APPEARANCE);
        // Valid Listing
        int lId = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, criteriaTypes, weights);
        
        assertNotEquals(-1, aId);
        assertNotEquals(-1, trId);
        assertNotEquals(-1, lId);

        String resume = "My Resume";
        String coverLetter = "My Cover Letter";
        try {
            int appId = Application.create(aId, lId, resume, coverLetter);
            assertNotEquals(-1, appId);
            ArrayList<Application> listingApps = new ArrayList<>(Application.getByListingId(lId));
            assertEquals(Application.getById(appId).toString(), listingApps.get(0).toString());
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            fail();
        }
        
    }

    @Test 
    public void declineAndAcceptTest() {
        // Create sample auditionee, talent recruiter, and listing
        int aId = Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1);
        int trId = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");

        ArrayList<Gender> genders = new ArrayList<>();
        genders.add(Gender.MALE);
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(Genre.ACTION);  
        ArrayList<CriteriaType> criteriaTypes = new ArrayList<>();
        int[] weights = new int[]{1};
        criteriaTypes.add(CriteriaType.PHYSICAL_APPEARANCE);
        // Valid Listing
        int lId = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, criteriaTypes, weights);
        
        assertNotEquals(-1, aId);
        assertNotEquals(-1, trId);
        assertNotEquals(-1, lId);

        String resume = "My Resume";
        String coverLetter = "My Cover Letter";
        try {
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
        int aId = Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1);
        int trId = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");

        ArrayList<Gender> genders = new ArrayList<>();
        genders.add(Gender.MALE);
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(Genre.ACTION);  
        ArrayList<CriteriaType> criteriaTypes = new ArrayList<>();
        int[] weights = new int[]{1};
        criteriaTypes.add(CriteriaType.PHYSICAL_APPEARANCE);
        // Valid Listing
        int lId = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, criteriaTypes, weights);
        
        assertNotEquals(-1, aId);
        assertNotEquals(-1, trId);
        assertNotEquals(-1, lId);

        String resume = "My Resume";
        String coverLetter = "My Cover Letter";
        try {
            int appId = Application.create(aId, lId, resume, coverLetter);
            assertNotEquals(-1, appId); 
            Application app = Application.getById(appId);
            app.score(CriteriaType.getId(CriteriaType.PHYSICAL_APPEARANCE.getName()), 50);
            app.setFinalScore();
            assertEquals(50, app.getFinalScore());
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }
}

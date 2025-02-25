package com.tems;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        assertTrue(Application.create(aId, lId, resume, coverLetter));
        
    }
}

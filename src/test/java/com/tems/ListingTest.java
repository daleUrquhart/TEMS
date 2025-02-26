package com.tems;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals; 
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.tems.models.CriteriaType;
import com.tems.models.Gender;
import com.tems.models.Genre;
import com.tems.models.Listing;
import com.tems.models.TalentRecruiter;
import com.tems.models.User;

public class ListingTest {
    
    @AfterEach 
    public void removeUsersAndListingsCreated() { 
        User.deleteAllUsers(); 
    }  

    @Test 
    public void createTest() {
        // Load test data
        int trId = TalentRecruiter.create("Bob", "bob@recruiter.ca", "Pass", "Talent Co.");
        ArrayList<Gender> genders = new ArrayList<>();
        genders.add(Gender.MALE);
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(Genre.ACTION); 
        genres.add(Genre.DRAMA);
        genres.add(Genre.ROMANCE); 
        ArrayList<CriteriaType> criteriaTypes = new ArrayList<>();
        int[] weights = new int[]{2};
        criteriaTypes.add(CriteriaType.PHYSICAL_APPEARANCE);

        // Valid Listing
        int id = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, criteriaTypes, weights);
        assertNotEquals(-1, id);  

        // Invalid empty genres
        ArrayList<Genre> emptyGenres = new ArrayList<>();
        id = Listing.create(trId, "Alice Bond", "Alice Bond role for the new Alice Bond movie by Movie Co.", genders, emptyGenres, criteriaTypes, weights);
        assertEquals(-1, id);

        // Invalid empty genders
        ArrayList<Gender> emptyGenders = new ArrayList<>();
        id = Listing.create(trId, "Kevin Bond", "Kevin Bond role for the new Kevin Bond movie by Movie Co.", emptyGenders, genres, criteriaTypes, weights);
        assertEquals(-1, id);  

        // Invalid empty criteria
        ArrayList<CriteriaType> emptyCriteria = new ArrayList<>();
        int[] emptyWeights = new int[0];
        id = Listing.create(trId, "Extra 7", "Extra for train chase scene in Trains 4", genders, genres, emptyCriteria, emptyWeights);
        assertEquals(-1, id);
    }

    @Test
    public void updateTest() {
        int trId = TalentRecruiter.create("Bob", "bob@recruiter.ca", "Pass", "Talent Co.");
        assertNotEquals(-1, trId);
        
        ArrayList<Gender> genders = new ArrayList<>();
        genders.add(Gender.MALE);
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(Genre.ACTION); 
        genres.add(Genre.DRAMA);
        genres.add(Genre.ROMANCE); 
        ArrayList<CriteriaType> criteriaTypes = new ArrayList<>();
        int[] weights = new int[]{2};
        criteriaTypes.add(CriteriaType.PHYSICAL_APPEARANCE);
        // Valid Listing
        int id = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, criteriaTypes, weights);
        assertNotEquals(id, -1); 
        
        Listing listing = Listing.getById(id);
        listing.setDescription("Updated Description");
        listing.setTitle("Updated Title");
        System.out.println(listing.toString());
        
        assertTrue(listing.update());
        assertEquals(listing.toString(), Listing.getById(listing.getListingId()).toString());
    }

    @Test
    public void getByTRIdTest() {
        // Build data
        int trId = TalentRecruiter.create("Jane", "jane@recruiter.ca", "Pass", "Talent Co.");
        assertNotEquals(-1, trId);
        
        ArrayList<Gender> genders = new ArrayList<>();
        genders.add(Gender.MALE);
        ArrayList<Gender> genders2 = new ArrayList<>();
        genders2.add(Gender.FEMALE);
        genders2.add(Gender.NON_BINARY);
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(Genre.ACTION); 
        genres.add(Genre.DRAMA);
        genres.add(Genre.ROMANCE); 
        ArrayList<CriteriaType> criteriaTypes = new ArrayList<>();
        int[] weights = new int[]{2};
        criteriaTypes.add(CriteriaType.PHYSICAL_APPEARANCE);
        // Valid Listings
        int id = Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, criteriaTypes, weights);
        int id2 = Listing.create(trId, "Alice Bond", "Alice Bond role for the new James Bond movie by Movie Co.", genders2, genres, criteriaTypes, weights);
        assertNotEquals(id, -1); 
        assertNotEquals(id2, -1); 
        ArrayList<Listing> trListings = Listing.getByTRId(trId);
        assertNotNull(trListings);
        for(Listing l : trListings) {
            System.out.println(l);
        }
    }
}

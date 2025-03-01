package com.tems.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.tems.models.Auditionee;
import com.tems.models.CriteriaType;
import com.tems.models.Gender;
import com.tems.models.Genre;
import com.tems.models.Listing;
import com.tems.models.TalentRecruiter;

public class SampleData {
    public static void load() {
        try {
            String hash = PasswordManager.hashPassword("a");

            // Define gender roles
            ArrayList<Gender> genderRoles = new ArrayList<>();
            genderRoles.add(Gender.MALE);
            genderRoles.add(Gender.FEMALE);

            // Define Criteria and Weights
            Map<CriteriaType, Integer> selectedCriteria = new HashMap<>();
            selectedCriteria.put(CriteriaType.PHYSICAL_APPEARANCE, 1);

            // Create auditionee
            Auditionee.create("John", "John@applicant.ca", hash, Gender.MALE, 1, genderRoles);

            // Create Talent Recruiter
            int trId = TalentRecruiter.create("John", "r@r.ca", hash, "Company 1");

            // Define Genders
            ArrayList<Gender> genders = new ArrayList<>();
            genders.add(Gender.MALE);
            ArrayList<Gender> genders2 = new ArrayList<>();
            genders2.add(Gender.FEMALE);

            // Define Genres
            ArrayList<Genre> genres = new ArrayList<>();
            genres.add(Genre.ACTION);

            // Create Listings
            Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, selectedCriteria);
            Listing.create(trId, "Alice Bond", "Alice Bond role for the new James Bond movie by Movie Co.", genders2, genres, selectedCriteria);

        } catch (SQLException e) {
            System.err.println("Error in SampleData.java: \n\t"+e.getMessage());
        }
    }
}

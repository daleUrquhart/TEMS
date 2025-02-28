package com.tems.util;

import java.sql.SQLException;
import java.util.ArrayList;

import com.tems.models.Auditionee;
import com.tems.models.CriteriaType;
import com.tems.models.Gender;
import com.tems.models.Genre;
import com.tems.models.Listing;
import com.tems.models.TalentRecruiter;

public class SampleData {
    public static void load() {
        try {
            Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1);
            int trId = TalentRecruiter.create("John", "john@company.ca", "Password", "Company 1");
            ArrayList<Gender> genders = new ArrayList<>();
            genders.add(Gender.MALE);
            ArrayList<Genre> genres = new ArrayList<>();
            genres.add(Genre.ACTION);  
            ArrayList<CriteriaType> criteriaTypes = new ArrayList<>();
            int[] weights = new int[]{1};
            criteriaTypes.add(CriteriaType.PHYSICAL_APPEARANCE);
            ArrayList<Gender> genders2 = new ArrayList<>();
            genders2.add(Gender.FEMALE);
            // Valid Listing
            Listing.create(trId, "James Bond", "James Bond role for the new James Bond movie by Movie Co.", genders, genres, criteriaTypes, weights);
            Listing.create(trId, "Alice Bond", "Alice Bond role for the new James Bond movie by Movie Co.", genders2, genres, criteriaTypes, weights);
        } catch (SQLException e) {
        }    
    }
}

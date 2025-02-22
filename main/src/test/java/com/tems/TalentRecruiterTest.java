package com.tems;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

import com.tems.models.TalentRecruiter;

public class TalentRecruiterTest {
    
    @Test
    public void createTRTest() {
        int status = TalentRecruiter.createTalentRecruiter("John", "john@company.ca", "Password", "Company 1");
        assertNotEquals(status, -1);
    }
}
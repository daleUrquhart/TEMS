package com.tems;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

import com.tems.models.Auditionee;
import com.tems.models.Gender;

public class AuditioneeTest {
    
    @Test 
    public void createAuditioneeTest() {
        int status = Auditionee.create("John", "John@applicant.ca", "Password", Gender.MALE, 1);
        assertNotEquals(status, -1);
    }
}

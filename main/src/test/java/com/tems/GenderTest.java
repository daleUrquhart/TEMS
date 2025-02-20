package com.tems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.tems.models.Gender;

public class GenderTest {

    /**
     * Test case to check the getDisplayName method for each gender
     */
    @Test
    public void testGetDisplayName() {
        // Testing for all gender constants to ensure their display names are correct
        assertEquals("Male", Gender.MALE.getDisplayName(), "The display name should be 'Male'");
        assertEquals("Female", Gender.FEMALE.getDisplayName(), "The display name should be 'Female'");
        assertEquals("Non-binary", Gender.NON_BINARY.getDisplayName(), "The display name should be 'Non-binary'");
        assertEquals("Transgender", Gender.TRANSGENDER.getDisplayName(), "The display name should be 'Transgender'");
        assertEquals("Gender Fluid", Gender.GENDER_FLUID.getDisplayName(), "The display name should be 'Gender Fluid'");
        assertEquals("Agender", Gender.AGENDER.getDisplayName(), "The display name should be 'Agender'");
        assertEquals("Bigender", Gender.BIGENDER.getDisplayName(), "The display name should be 'Bigender'");
        assertEquals("Two-Spirit", Gender.TWO_SPIRIT.getDisplayName(), "The display name should be 'Two-Spirit'");
        assertEquals("Other", Gender.OTHER.getDisplayName(), "The display name should be 'Other'");
    }

    /**
     * Test case to check if fromString correctly maps strings to the enum constants
     */
    @Test
    public void testFromString() {
        assertEquals(Gender.MALE, Gender.fromString("Male"), "The gender should be Male");
        assertEquals(Gender.FEMALE, Gender.fromString("Female"), "The gender should be Female");
        assertEquals(Gender.NON_BINARY, Gender.fromString("Non-binary"), "The gender should be Non-binary");
        assertEquals(Gender.TRANSGENDER, Gender.fromString("Transgender"), "The gender should be Transgender");
        assertEquals(Gender.GENDER_FLUID, Gender.fromString("Gender Fluid"), "The gender should be Gender Fluid");
        assertEquals(Gender.AGENDER, Gender.fromString("Agender"), "The gender should be Agender");
        assertEquals(Gender.BIGENDER, Gender.fromString("Bigender"), "The gender should be Bigender");
        assertEquals(Gender.TWO_SPIRIT, Gender.fromString("Two-Spirit"), "The gender should be Two-Spirit");
        assertEquals(Gender.OTHER, Gender.fromString("Other"), "The gender should be Other");
    }

    /**
     * Test case to check if fromString throws IllegalArgumentException for invalid input
     */
    @Test
    public void testFromStringInvalidInput() {
        // Testing invalid input that doesn't match any of the enum constants
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Gender.fromString("InvalidGender");
        });
        assertEquals("No gender found with name: InvalidGender", exception.getMessage(), "The error message should match the expected.");
    }
}

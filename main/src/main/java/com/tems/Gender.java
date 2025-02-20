package com.tems;

/**
 * Represents gender options for users for data integrety and consistencey
 * @author Dale Urquhart
 */
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    NON_BINARY("Non-binary"),
    TRANSGENDER("Transgender"),
    GENDER_FLUID("Gender Fluid"),
    AGENDER("Agender"),
    BIGENDER("Bigender"),
    TWO_SPIRIT("Two-Spirit"),
    OTHER("Other");

    /**
     * String representation of enum constant
     */
    private String displayName;

    /**
     * Constructor assigns the string version of the enum
     * @param displayName Display name of the enum
     */
    Gender(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the enum 
     * @return Display name of the enum 
     */
    public String getDisplayName() {
        return displayName;
    }
 
    /**
     * Gets enum constant from string value
     * @param text String representation to find constant for
     * @return Enum constant of string value
     * @throws IllegalArgumentException For when String value has no asssociated constant
     */
    public static Gender fromString(String text) throws IllegalArgumentException{
        for (Gender gender : Gender.values()) {
            if (gender.displayName.equalsIgnoreCase(text)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("No gender found with name: " + text);
    }
}

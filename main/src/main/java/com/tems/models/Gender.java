package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tems.util.ConnectionManager;

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
    private final String displayName;

    /**
     * Constructor assigns the string version of the enum
     * @param displayName Display name of the enum
     */
    Gender(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the Genders id of the gender
     * @param gender
     * @return
     */
    public int getId() throws SQLException{
        String sql = "SELECT * FROM Genders WHERE gender_name = ?";
        String gender_name = getDisplayName();

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gender_name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("gender_id");
                } else {
                    throw new SQLException("No user found with name: " + gender_name);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching gender by name: " + e.getMessage());
            throw e; 
        }
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

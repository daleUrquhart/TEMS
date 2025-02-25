


package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.tems.util.ConnectionManager;

public enum CriteriaType {
    PHYSICAL_APPEARANCE("Physical Appearance"),
    STAR_POWER("Star Power & Marketability"),
    LANGUAGE_ACCENT("Language & Accent Requirements"),
    SCRIPT_TIMING("Script Timing"),
    VOICE_MODULATION("Voice Modulation"),
    PHYSICAL_SIMILARITY("Physical Similarity to Role"), 
    ACTING_SKILLS("Acting Skills"),
    EMOTIONAL_RANGE("Emotional Range"),
    COMEDIC_TIMING("Comedic Timing"),
    SCREEN_PRESENCE("Screen Presence"),
    CHARISMA("Charisma"),
    REHEARSAL_ABILITY("Rehearsal Ability"), 
    RELIABILITY("Reliability"),
    PUNCTUALITY("Punctuality"),
    AVAILABILITY("Availability for Shoot Dates"),
    TEAMWORK("Teamwork & Collaboration"), 
    ACTION_SCENE_ABILITY("Action Scene Ability"),
    DANCE_SKILLS("Dance Skills"),
    COMBAT_SKILLS("Combat Skills"),
    SINGING_ABILITY("Singing Ability");

    private final String name;

    // Constructor
    CriteriaType(String name) {
        this.name = name;
    }

    // Getter for the name of the criteria
    public String getName() {
        return name;
    }

    /**
     * Gets enum constant from string value
     * @param text String representation to find constant for
     * @return Enum constant of string value
     * @throws IllegalArgumentException For when String value has no asssociated constant
     */
    public static CriteriaType fromString(String text) throws IllegalArgumentException{
        for (CriteriaType CriteriaType : CriteriaType.values()) {
            if (CriteriaType.name.equalsIgnoreCase(text)) {
                return CriteriaType;
            }
        }
        throw new IllegalArgumentException("No CriteriaType found with name: " + text);
    }

    // Method to ArrayList all available criteria types
    public static ArrayList<String> getAllCriteriaNames() {
        ArrayList<String> criteriaNames = new ArrayList<>();
        for (CriteriaType criteria : CriteriaType.values()) {
            criteriaNames.add(criteria.getName());
        }
        return criteriaNames;
    }

    public static int getId(String criteriaName) {
        String sql = "SELECT * FROM Criteria WHERE criteria_name = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, criteriaName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("criteria_id");
                } else {
                    throw new SQLException("No criteria found with name: " + criteriaName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching criteria by name: " + e.getMessage());
            return -1;
        }
    }

    public int getId() {
        String sql = "SELECT * FROM Criteria WHERE criteria_name = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            String criteriaName = getName();
            stmt.setString(1, criteriaName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("criteria_id");
                } else {
                    throw new SQLException("No criteria found with name: " + criteriaName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching criteria by name: " + e.getMessage());
            return -1;
        }
    }

    public static CriteriaType getById(int id) {
        String sql = "SELECT * FROM Criteria WHERE criteria_id = ?";
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return CriteriaType.fromString(rs.getString("criteria_name"));
                }
            }
        } catch(SQLException e) {
            System.err.println("Error getting criteria by Id: "+e.getMessage());
        }
        return null;
    } 
}

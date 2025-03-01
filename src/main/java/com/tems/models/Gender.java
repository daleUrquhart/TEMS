package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // CRUD Operations
    /**
     * Gets the Genders id of the gender
     * @param gender
     * @return
     */
    public int getId() {
        String sql = "SELECT * FROM Genders WHERE gender_name = ?";
        String gender_name = getDisplayName();
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gender_name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("gender_id");
                } else {
                    throw new SQLException("No gender found with name: " + gender_name);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching gender by name: " + e.getMessage());
            return -1;
        }
    }

    public static ArrayList<Gender> getAll() throws SQLException {
        String sql = "SELECT * FROM Genders";
        ArrayList<Gender> genders = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) { 

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                genders.add(Gender.getById(rs.getInt("gender_id")));
            } 
            return genders;
        } catch (SQLException e) {
            throw new SQLException("Error fetching gender by name: " + e.getMessage());
        }
    }

    public static Gender getById(int id) { 
        String sql = "SELECT * FROM Genders WHERE gender_id = ?";
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return Gender.fromString(rs.getString("gender_name"));
                }
            }
        } catch(SQLException e) {
            System.err.println("Error getting gender by Id: "+e.getMessage());
        }
        return null;
    } 

    public static List<Gender> getByListingId(int id) {
        String sql = "SELECT * FROM ListingGenderRoles WHERE listing_id = ?";
        ArrayList<Gender> genders = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { 
                    genders.add(Gender.getById(rs.getInt("gender_id"))); 
                } 
            }
            return genders;
        } catch (SQLException e) {
            System.err.println("Error fetching gender by name: " + e.getMessage());
            return null;
        }
    } 

    @Override
    public String toString() {
        return getDisplayName()+" ";
    }
}

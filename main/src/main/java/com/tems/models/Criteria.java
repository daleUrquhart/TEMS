package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.tems.util.ConnectionManager;
import com.tems.util.Env;

/**
 * Manages criteria which may be needed for a Listing
 * @author Dale Urquhart
 */
public class Criteria {
    private int criteriaTypeId; 
    private int weight; 
    private final static int RANGE = 100;  
    private final int listingId;

    public Criteria(int criteriaTypeId, int listingId, int weight) {
        this.criteriaTypeId = criteriaTypeId;
        this.listingId = listingId; 
        this.weight = weight;
    } 

    // Getters and setters
    public int getListingId() { return listingId; }
    public int  getCriteriaTypeId() { return criteriaTypeId; }
    public int getWeight() { return weight; }
    public int getRange() { return RANGE; }

    public void setCriteriaTypeId(int criteriaTypeId) { this.criteriaTypeId = criteriaTypeId; }
    public void setWeight(int weight) { this.weight = weight; }

    // CRUD Operations
    // Create a ListingCriteria
    public static boolean create(int listingId, int criteriaTypeId, int weight) {
        String sql = "INSERT INTO ListingCriteria (listing_id, criteria_id, weight) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, listingId);
            stmt.setInt(2, criteriaTypeId);
            stmt.setInt(3, weight);

            return stmt.executeUpdate() > 0;

        } catch(SQLException e) {
            System.err.println("Error creating criteria: " + e.getMessage());
            return false;
        }
    }

    // Update listing criteria
    public boolean update() {
        String sql = String.format("UPDATE ListingCriteria SET weight = ? WHERE listing_id = ? AND criteira_id = ?", Env.WEIGHT);

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setInt(2, getWeight());
            stmt.setInt(4, getListingId());
            stmt.setInt(5, getCriteriaTypeId());
            return stmt.executeUpdate() > 0;

        } catch(SQLException e) {
            System.err.println("Error updating criteria: " + e.getMessage());
            return false;
        }
    }

    // Delete a listing criteria
    public boolean delete() {
        String sql = "DELETE FROM ListingCriteria WHERE criteria_id = ? AND listing_id = ?";
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, getCriteriaTypeId());
            stmt.setInt(2, getListingId());

            return stmt.executeUpdate() > 0;
        } catch(SQLException e) {
            System.err.println(String.format("Error deleting criteria with id %d: %s", getCriteriaTypeId(), e.getMessage()));
            return false;
        }
    }

    // Gets All Listing criteria by a listing id
    public static ArrayList<Criteria> getByListingId(int listingId) {
        String sql = "SELECT * FROM ListingCriteria WHERE listing_id = ?";
        ArrayList<Criteria> criteria = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, listingId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    criteria.add(new Criteria(
                        rs.getInt("criteria_id"),
                        rs.getInt("listing_id"), 
                        rs.getInt("weight")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching criteria for listing id " + listingId + ": " + e.getMessage());
        }
        
        return criteria;
    } 

    /**
     * String representation of the criteria for debugging
     * @return String representation of the criteria
     */
    @Override 
    public String toString() {
        return String.format("Name: %s, Weight: %d, Range: %d ", CriteriaType.getById(getCriteriaTypeId()).getName(), getWeight(), getRange());
    }
}

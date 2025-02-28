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
    private final int CRITERIA_TYPE_ID; 
    private int weight; 
    private final static int RANGE = 100;  
    private final int LISTING_ID;

    public Criteria(int criteriaTypeId, int listingId, int weight) {
        this.CRITERIA_TYPE_ID = criteriaTypeId;
        this.LISTING_ID = listingId; 
        this.weight = weight;
    } 

    // Getters and setters
    public int getListingId() { return LISTING_ID; }
    public int  getCriteriaTypeId() { return CRITERIA_TYPE_ID; }
    public int getWeight() { return weight; }
    public int getRange() { return RANGE; }

    public void setWeight(int weight) { this.weight = weight; }

    // CRUD Operations
    // Create a ListingCriteria
    public static void create(int listingId, int criteriaTypeId, int weight) throws SQLException{
        String sql = "INSERT INTO ListingCriteria (listing_id, criteria_id, weight) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, listingId);
            stmt.setInt(2, criteriaTypeId);
            stmt.setInt(3, weight);

            if(stmt.executeUpdate() == 0) throw new SQLException("No rows updated for creating criteria with listing id " + listingId + " and criteria id of "+criteriaTypeId);

        } catch(SQLException e) {
            throw new SQLException("Error creating criteria: \n\t" + e.getMessage());
        }
    }

    // Update listing criteria
    public void update() throws SQLException {
        String sql = String.format("UPDATE ListingCriteria SET weight = ? WHERE listing_id = ? AND criteira_id = ?", Env.WEIGHT);

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setInt(2, getWeight());
            stmt.setInt(4, getListingId());
            stmt.setInt(5, getCriteriaTypeId());
            if(stmt.executeUpdate() == 0) throw new SQLException("No rows updated for updating criteria with listing id " + getListingId() + " and criteria id of "+getCriteriaTypeId());

        } catch(SQLException e) {
            throw new SQLException("Error updating criteria: \n\t" + e.getMessage());
        }
    }

    // Delete a listing criteria
    public void delete() throws SQLException {
        String sql = "DELETE FROM ListingCriteria WHERE criteria_id = ? AND listing_id = ?";
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, getCriteriaTypeId());
            stmt.setInt(2, getListingId());

            if(stmt.executeUpdate() == 0) throw new SQLException(String.format("No rows updated for deleting criteria with id %d", getCriteriaTypeId()));
        } catch(SQLException e) {
            throw new SQLException(String.format("Error deleting criteria with id %d: %s", getCriteriaTypeId(), e.getMessage()));
        }
    }

    // Gets All Listing criteria by a listing id
    public static ArrayList<Criteria> getByListingId(int listingId) throws SQLException { 
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
                return criteria;
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching criteria for listing id " + listingId + ": \n\t" + e.getMessage());
        }
    }

    // Gets All Listing criteria by a listing id and criteria id
    public static Criteria getByListingAndTypeId(int listingId, int criteriaTypeId) throws SQLException{
        String sql = "SELECT * FROM ListingCriteria WHERE listing_id = ? AND criteria_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, listingId);
            stmt.setInt(2, criteriaTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Criteria(
                        rs.getInt("criteria_id"),
                        rs.getInt("listing_id"), 
                        rs.getInt("weight")
                    );
                } throw new SQLException("No results found for listing id " + listingId + " and type id : "+criteriaTypeId);
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching criteria for listing id " + listingId + " and type id : "+criteriaTypeId + "\n\t: " + e.getMessage());
        }
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

package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.tems.util.ConnectionManager;

/**
 * Manages criteria which may be needed for a Listing
 * @author Dale Urquhart
 */
public class Criteria {
    
    private String name;
    private int weight; 
    private final static int RANGE = 100;  
    private final int criteriaId; 
    private final int listingId;

    public Criteria(int criteriaId, String name, int weight, int listingId) {
        this.name = name;
        this.criteriaId = criteriaId;
        this.weight = weight;
        this.listingId = listingId; 
    } 

    // Getters and setters
    public int getListingId() { return listingId; }
    public String getName() { return name; }
    public int getWeight() { return weight; }
    public int getRange() { return RANGE; }
    public int getCriteriaId() { return criteriaId; }

    public void setName(String name) { this.name = name; }
    public void setWeight(int weight) { this.weight = weight; }

    // CRUD Operations
    public static boolean create(int listingId, String name, int weight) {
        String sql = "INSERT INTO Criteria (listing_id, name, weight) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, listingId);
            stmt.setString(2, name);
            stmt.setInt(3, weight);

            return stmt.executeUpdate() > 0;

        } catch(SQLException e) {
            System.err.println("Error creating criteria: " + e.getMessage());
            return false;
        }
    }

    public boolean update() {
        String sql = "UPDATE Criteria SET criteria_name = ?, weight = ? WHERE criteria_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setString(1, getName());
            stmt.setInt(2, getWeight());
            stmt.setInt(3, getCriteriaId());

            return stmt.executeUpdate() > 0;

        } catch(SQLException e) {
            System.err.println("Error updating criteria: " + e.getMessage());
            return false;
        }
    }

    public boolean delete() {
        String sql = "DELETE FROM Criteria WHERE criteria_id = ?";
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, getCriteriaId());

            return stmt.executeUpdate() > 0;
        } catch(SQLException e) {
            System.err.println(String.format("Error deleting criteria with id %d: %s", getCriteriaId(), e.getMessage()));
            return false;
        }
    }

    public static ArrayList<Criteria> getCriteriaForListingId(int listingId) {
        String sql = "SELECT * FROM Criteria WHERE listing_id = ?";
        ArrayList<Criteria> criteria = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, listingId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    criteria.add(new Criteria(
                        rs.getInt("criteria_id"),
                        rs.getString("name"), 
                        rs.getInt("weight"),
                        listingId
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
        return String.format("Criteria[Weight=%d, Range=%d]", getWeight(), getRange());
    }
}

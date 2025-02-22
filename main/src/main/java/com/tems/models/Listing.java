package com.tems.models; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.tems.util.ConnectionManager;

public class Listing {
    
    private final int listingId;
    private final int recruiterId;
    private String title;
    private String description;
    private final Timestamp createdAt;

    public Listing(int listingId, int recruiterId, String title, String description, Timestamp createdAt) {
        this.listingId = listingId;
        this.recruiterId = recruiterId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getListingId() { return listingId; }
    public int getRecruiterId() { return recruiterId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; } 

    //CRUD operations
    public static boolean create(int recruiterId, String title, String description, Timestamp createdAt, ArrayList<Gender> gender_roles, ArrayList<Genre> genres) { 
        String sql = "INSERT INTO Listings (recruiter_id, title, description, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, recruiterId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setTimestamp(4, createdAt);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating listing: " + e.getMessage());
        }
        return false;
    }
 

    /**
     * Deletes a listing by ID.
     */
    public static boolean delete(int listingId) {
        String sql = "DELETE FROM Listings WHERE listing_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listingId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting listing: " + e.getMessage());
            return false;
        }
    }


    /**
     * Updates the user's information in the database based on the instance data.
     */
    public boolean update() {
        String sql = "UPDATE Users SET title = ?, description = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, getTitle());
            stmt.setString(2, getDescription()); 

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating listing: " + e.getMessage());
            return false;
        }
    } 

    /**
     * Gets all listings managed by the specified talent recruiter
     * @param id TR id to search listings by
     * @return 
     */
    public static ArrayList<Listing> getListingsByTRId(int recruiterId) {
        String sql = "SELECT * FROM listings WHERE recruiter_id = ?";
        ArrayList<Listing> listings = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    listings.add(new Listing(
                            rs.getInt("listing_id"),
                            rs.getInt("recruiter_id"),
                            rs.getString("title"),
                            rs.getString("description"), 
                            rs.getTimestamp("created_at")
                    ));
                }
            } 
        } catch (SQLException e) {
            System.err.println("Error fetching listing by TR id: " + e.getMessage());
        } 

        return listings;
    }  
}

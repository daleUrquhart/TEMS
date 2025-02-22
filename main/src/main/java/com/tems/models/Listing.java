package com.tems.models; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.tems.util.ConnectionManager;

public class Listing {
    
    public Listing(int listingId, int recruiterId, String title, String description, String genre, Gender genderRole, Timestamp createdAt) {

    }

    /**
     * Gets all listings managed by the specified talent recruiter
     * @param id TR id to search listings by
     * @return 
     */
    public static ArrayList<Listing> getListingsByTRId(int recruiterId) throws SQLException{
        String sql = "SELECT * FROM listings WHERE recruiter_id = ?";
        ArrayList<Listing> listings = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(2, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    listings.add(new Listing(
                            rs.getInt("listing_id"),
                            rs.getInt("recruiter_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("genre"),
                            Gender.fromString(rs.getString("gender_role")),
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

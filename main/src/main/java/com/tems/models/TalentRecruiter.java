package com.tems.models; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.tems.util.ConnectionManager; 

/**
 * TalentRecruiter model
 * @author Dale Urquhart
 */
public class TalentRecruiter extends User{ 

    public TalentRecruiter(int trId, String name, String email, String passwordHash, String role) {
        super(trId, name, email, passwordHash, role); 
    }

    /**
     * Gets all listings managed by the specified talent recruiter
     * @param id TR id to search listings by
     * @return 
     */
    public static ArrayList<Listing> getListings(int recruiterId) throws SQLException{
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
            System.err.println("Error fetching user by email: " + e.getMessage());
        } 

        return listings;
    }

    public void addListing(Listing listing) {  }

    public boolean removeListing(Listing listing) { return true; }  
}

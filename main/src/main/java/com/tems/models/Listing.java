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
    public static int create(int recruiterId, String title, String description, ArrayList<Gender> genderRoles, ArrayList<Genre> genres, ArrayList<CriteriaType> criteria, int[] weights) {
        String sqlListing = "INSERT INTO Listings (recruiter_id, title, description) VALUES (?, ?, ?)";
        if(genres.isEmpty() || genderRoles.isEmpty() || criteria.isEmpty() || criteria.size() != weights.length) return -1; 

        try (Connection conn = ConnectionManager.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtListing = conn.prepareStatement(sqlListing, Statement.RETURN_GENERATED_KEYS)) {
                stmtListing.setInt(1, recruiterId);
                stmtListing.setString(2, title);
                stmtListing.setString(3, description);
                
                int affectedRows = stmtListing.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return -1;
                }
                
                // Retrieve generated listing_id
                int listingId;
                try (ResultSet rs = stmtListing.getGeneratedKeys()) {
                    if (rs.next()) {
                        listingId = rs.getInt(1);
                    } else {
                        conn.rollback();
                        return -1;
                    }
                }
                
                // Insert into ListingGenderRoles for each gender role
                String sqlGender = "INSERT INTO ListingGenderRoles (listing_id, gender_id) VALUES (?, ?)";
                for (Gender gender : genderRoles) {
                    try (PreparedStatement stmtGender = conn.prepareStatement(sqlGender)) {
                        stmtGender.setInt(1, listingId);
                        stmtGender.setInt(2, gender.getId());
                        stmtGender.executeUpdate();
                    }
                }
                
                // Insert into ListingGenres for each genre
                String sqlGenre = "INSERT INTO ListingGenres (listing_id, genre_id) VALUES (?, ?)";
                for (Genre genre : genres) {
                    try (PreparedStatement stmtGenre = conn.prepareStatement(sqlGenre)) {
                        stmtGenre.setInt(1, listingId);
                        stmtGenre.setInt(2, genre.getId());
                        stmtGenre.executeUpdate();
                    }
                }
                
                // Insert into ListingCriteria for each criteria // TODO
                String sqlCriteria = "INSERT INTO ListingCriteria (listing_id, criteria_id, weight) VALUES (?, ?, ?)";
                for (int i = 0; i < criteria.size(); i++) {
                    try (PreparedStatement stmtCriteria = conn.prepareStatement(sqlCriteria)) {
                        stmtCriteria.setInt(1, listingId);
                        stmtCriteria.setInt(2, criteria.get(i).getId());
                        stmtCriteria.setInt(3, weights[i]); 
                        stmtCriteria.executeUpdate();
                    }
                }

                // If all inserts succeeded, commit the transaction                                             
                conn.commit();                                  
 
                return listingId;
            } catch (SQLException e) {
                System.err.println("Error creating listing or its associations: " + e.getMessage());
                conn.rollback(); // Rollback if any error occurs
                return -1;
            } finally { 
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection or transaction: " + e.getMessage());
            return -1;
        }
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

    public boolean update() {
        String sql = "UPDATE Listings SET title = ?, description = ? WHERE listing_id = ?";
    
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, getTitle());
            stmt.setString(2, getDescription());
            stmt.setInt(3, getListingId()); 
    
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
    public static ArrayList<Listing> getByTRId(int recruiterId) {
        String sql = "SELECT * FROM listings WHERE recruiter_id = ?";
        ArrayList<Listing> listings = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recruiterId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listings.add(new Listing(
                        rs.getInt("listing_id"),
                        rs.getInt("recruiter_id"),
                        rs.getString("title"),
                        rs.getString("description"), 
                        rs.getTimestamp("created_at")
                ));
            }  
        } catch (SQLException e) {
            System.err.println("Error fetching listing by TR id: " + e.getMessage());
        } 

        return listings;
    }  

    public static void deleteAll() {
        String sql = "DELETE FROM Listings";  
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {  
             
            stmt.execute(sql); 
            
        } catch (SQLException e) {
            System.err.println("Error resetting listings: " + e.getMessage());
        }
    }
    
    public static Listing getById(int id) {
        String sql = "SELECT * FROM Listings WHERE listing_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Listing(
                            rs.getInt("listing_id"),
                            rs.getInt("recruiter_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getTimestamp("created_at")
                    );
                } else {
                    throw new SQLException("No listing found with id: " + id);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching listing by id: " + e.getMessage()); 
            return null;
        }

    }

    @Override
    public String toString() {
        try {
            String gendersString = "", genresString = "", criteriaString = "";
            for(Gender g : Gender.getByListingId(getListingId())) { gendersString += g.toString(); }
            for(Genre g : Genre.getByListingId(getListingId())) { genresString += g.toString(); }
            for(Criteria c : Criteria.getByListingId(getListingId())) { criteriaString += c.toString(); }
            return String.format("%s\n%s\nGender Roles:%s\nGenres:%s\nCriteria:%s\nManaging Recruiter:%s\nCreated At:%s",
                getTitle(), getDescription(), gendersString, genresString, criteriaString, TalentRecruiter.getById(getRecruiterId()).toString(), getCreatedAt());
    
        } catch(IllegalArgumentException e) {
            System.err.println("Error getting Listing.toString(): "+e.getMessage());
            return null;
        } catch(NullPointerException e) {
            System.err.println("Null ptr exception (likely from TR Id which is: "+getRecruiterId()+"): "+e.getMessage());
            return null;
        } catch(SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}

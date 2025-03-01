package com.tems.models; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tems.util.ConnectionManager;

public class Listing {
    
    private final int LISTING_ID;
    private final int RECRUITER_ID;
    private final Timestamp CREATED_AT;
    private String title;
    private String description;
    private Map<CriteriaType, Integer> criteria;
    private List<Gender> genderRoles;
    private List<Genre> genres;

    public Listing(int listingId, int recruiterId, String title, String description, Timestamp createdAt, Map<CriteriaType, Integer> criteria, List<Gender> genderRoles, List<Genre> genres) {
        this.LISTING_ID = listingId;
        this.RECRUITER_ID = recruiterId;
        this.CREATED_AT = createdAt;
        this.title = title;
        this.description = description; 
        this.genres = genres;
        this.genderRoles = genderRoles;
        this.criteria = criteria;
    }

    // Getters and setters
    public int getListingId() { return LISTING_ID; }
    public int getRecruiterId() { return RECRUITER_ID; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Timestamp getCreatedAt() { return CREATED_AT; }
    public List<Gender> getGenderRoles() { return genderRoles; }
    public List<Genre> getGenres() { return genres; }
    public Map<CriteriaType, Integer> getCriteria() { return criteria; }


    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; } 
    public void setGenres(List<Genre> genres) { this.genres = genres; }
    public void setGenders(List<Gender> genderRoles) { this.genderRoles = genderRoles; }
    public void setCriteria(Map<CriteriaType, Integer> criteria) { this.criteria = criteria; }

    //CRUD operations
    public static int create(int recruiterId, String title, String description, List<Gender> genderRoles, List<Genre> genres, Map<CriteriaType, Integer> selectedCriteria) throws SQLException {
        String sqlListing = "INSERT INTO Listings (recruiter_id, title, description) VALUES (?, ?, ?)";
    
        if (genres.isEmpty() || genderRoles.isEmpty() || selectedCriteria.isEmpty()) return -1;
    
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
                    throw new SQLException("No rows updated on creating listing");
                }
    
                // Retrieve generated listing_id
                int listingId;
                try (ResultSet rs = stmtListing.getGeneratedKeys()) {
                    if (rs.next()) {
                        listingId = rs.getInt(1);
                    } else {
                        conn.rollback();
                        throw new SQLException("Error getting generated keys on created listing.");
                    }
                }
    
                // Insert into ListingGenderRoles for each gender role
                String sqlGender = "INSERT INTO ListingGenderRoles (listing_id, gender_id) VALUES (?, ?)";
                try (PreparedStatement stmtGender = conn.prepareStatement(sqlGender)) {
                    for (Gender gender : genderRoles) {
                        stmtGender.setInt(1, listingId);
                        stmtGender.setInt(2, gender.getId());
                        stmtGender.addBatch();
                    }
                    stmtGender.executeBatch();
                }
    
                // Insert into ListingGenres for each genre
                String sqlGenre = "INSERT INTO ListingGenres (listing_id, genre_id) VALUES (?, ?)";
                try (PreparedStatement stmtGenre = conn.prepareStatement(sqlGenre)) {
                    for (Genre genre : genres) {
                        stmtGenre.setInt(1, listingId);
                        stmtGenre.setInt(2, genre.getId());
                        stmtGenre.addBatch();
                    }
                    stmtGenre.executeBatch();
                }
    
                // Insert into ListingCriteria for each criteria with its weight
                String sqlCriteria = "INSERT INTO ListingCriteria (listing_id, criteria_id, weight) VALUES (?, ?, ?)";
                try (PreparedStatement stmtCriteria = conn.prepareStatement(sqlCriteria)) {
                    for (Map.Entry<CriteriaType, Integer> entry : selectedCriteria.entrySet()) {
                        stmtCriteria.setInt(1, listingId);
                        stmtCriteria.setInt(2, entry.getKey().getId());
                        stmtCriteria.setInt(3, entry.getValue());
                        stmtCriteria.addBatch();
                    }
                    stmtCriteria.executeBatch();
                }
    
                // Commit transaction if all inserts succeed
                conn.commit();
                return listingId;
            } catch (SQLException e) {
                conn.rollback(); // Rollback if any error occurs
                throw new SQLException("Error creating listing or its associations: \n\t" + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new SQLException("Error with connection or transaction: \n\t" + e.getMessage());
        }
    }
    

    /**
     * Deletes a listing by ID.
     */
    public static void delete(int listingId) throws SQLException {
        String sql = "DELETE FROM Listings WHERE listing_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listingId);

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) throw new SQLException("No rows changed on deleting listing with id " + listingId);

        } catch (SQLException e) {
            throw new SQLException("Error deleting listing: \n\t" + e.getMessage());
        }
    }

    public void update() throws SQLException {
        String sqlUpdateListing = "UPDATE Listings SET title = ?, description = ? WHERE listing_id = ?";
        String sqlDeleteGenderRoles = "DELETE FROM ListingGenderRoles WHERE listing_id = ?";
        String sqlInsertGenderRoles = "INSERT INTO ListingGenderRoles (listing_id, gender_id) VALUES (?, ?)";
        String sqlDeleteGenres = "DELETE FROM ListingGenres WHERE listing_id = ?";
        String sqlInsertGenres = "INSERT INTO ListingGenres (listing_id, genre_id) VALUES (?, ?)";
        String sqlDeleteCriteria = "DELETE FROM ListingCriteria WHERE listing_id = ?";
        String sqlInsertCriteria = "INSERT INTO ListingCriteria (listing_id, criteria_id, weight) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUpdateListing = conn.prepareStatement(sqlUpdateListing)) {
                // Update listing title and description
                stmtUpdateListing.setString(1, getTitle());
                stmtUpdateListing.setString(2, getDescription());
                stmtUpdateListing.setInt(3, getListingId());

                int affectedRows = stmtUpdateListing.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    throw new SQLException("No rows updated for listing with id " + getListingId());
                }
            }

            // Update gender roles
            try (PreparedStatement stmtDeleteGenderRoles = conn.prepareStatement(sqlDeleteGenderRoles);
                PreparedStatement stmtInsertGenderRoles = conn.prepareStatement(sqlInsertGenderRoles)) {
                
                stmtDeleteGenderRoles.setInt(1, getListingId());
                stmtDeleteGenderRoles.executeUpdate();

                for (Gender gender : getGenderRoles()) {
                    stmtInsertGenderRoles.setInt(1, getListingId());
                    stmtInsertGenderRoles.setInt(2, gender.getId());
                    stmtInsertGenderRoles.addBatch();
                }
                stmtInsertGenderRoles.executeBatch();
            }

            // Update genres
            try (PreparedStatement stmtDeleteGenres = conn.prepareStatement(sqlDeleteGenres);
                PreparedStatement stmtInsertGenres = conn.prepareStatement(sqlInsertGenres)) {
                
                stmtDeleteGenres.setInt(1, getListingId());
                stmtDeleteGenres.executeUpdate();

                for (Genre genre : getGenres()) {
                    stmtInsertGenres.setInt(1, getListingId());
                    stmtInsertGenres.setInt(2, genre.getId());
                    stmtInsertGenres.addBatch();
                }
                stmtInsertGenres.executeBatch();
            }

            // Update criteria
            try (PreparedStatement stmtDeleteCriteria = conn.prepareStatement(sqlDeleteCriteria);
                PreparedStatement stmtInsertCriteria = conn.prepareStatement(sqlInsertCriteria)) {
                
                stmtDeleteCriteria.setInt(1, getListingId());
                stmtDeleteCriteria.executeUpdate();

                for (Map.Entry<CriteriaType, Integer> entry : getCriteria().entrySet()) {
                    stmtInsertCriteria.setInt(1, getListingId());
                    stmtInsertCriteria.setInt(2, entry.getKey().getId());
                    stmtInsertCriteria.setInt(3, entry.getValue());
                    stmtInsertCriteria.addBatch();
                }
                stmtInsertCriteria.executeBatch();
            }

            // Commit transaction
            conn.commit();
        } catch (SQLException e) {
            throw new SQLException("Error updating listing or its associations: \n\t" + e.getMessage());
        }
    }

     
    /**
     * Gets all listings managed by the specified talent recruiter
     * @param id TR id to search listings by
     * @return 
     */
    public static ArrayList<Listing> getByTRId(int recruiterId) throws SQLException{
        String sql = "SELECT * FROM listings WHERE recruiter_id = ?";
        ArrayList<Listing> listings = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recruiterId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int lId = rs.getInt("listing_id");
                listings.add(new Listing(
                        lId,
                        rs.getInt("recruiter_id"),
                        rs.getString("title"),
                        rs.getString("description"), 
                        rs.getTimestamp("created_at"),
                        Criteria.getByListingId(lId),
                        Gender.getByListingId(lId),
                        Genre.getByListingId(lId)
                        
                ));
            }  
        } catch (SQLException e) {
            throw new SQLException("Error fetching listing by TR id: \n\t" + e.getMessage());
        } 

        return listings;
    }  

    public static void deleteAll() throws SQLException{
        String sql = "DELETE FROM Listings";  
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {  
             
            stmt.execute(sql); 
            
        } catch (SQLException e) {
            throw new SQLException("Error resetting listings: \n\t" + e.getMessage());
        }
    }
    
    public static Listing getById(int id) throws SQLException {
        String sql = "SELECT * FROM Listings WHERE listing_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int lId = rs.getInt("listing_id");
                    return new Listing(
                            lId,
                            rs.getInt("recruiter_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getTimestamp("created_at"),
                            Criteria.getByListingId(lId),
                            Gender.getByListingId(lId),
                            Genre.getByListingId(lId)
                    );
                } else {
                    throw new SQLException("No listing found with id: " + id);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching listing by id: \n\t" + e.getMessage()); 
        }

    }

    public static ArrayList<Listing> getByGenders(ArrayList<Gender> genders) throws SQLException {
        String sql = "SELECT * FROM ListingGenderRoles WHERE gender_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ArrayList<Listing> listings = new ArrayList<>();
            for(Gender gender : genders) {
                stmt.setInt(1, gender.getId());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        listings.add(Listing.getById(rs.getInt("listing_id")));
                    }  
                } catch (SQLException e) {
                    throw new SQLException("Querying ListingGenres listing by id: \n\t" + e.getMessage()); 
                }
            }
            return listings;
            
        } catch (SQLException e) {
            throw new SQLException("Error fetching listing by id: \n\t" + e.getMessage()); 
        }

    }

    @Override
    public String toString() {
        try {
            String gendersString = "", genresString = "", criteriaString = "";
            for(Gender g : Gender.getByListingId(getListingId())) { gendersString += g.toString(); }
            for(Genre g : Genre.getByListingId(getListingId())) { genresString += g.toString(); }
            for(Map.Entry<CriteriaType, Integer> entry : Criteria.getByListingId(getListingId()).entrySet()) { criteriaString += entry.getKey().getName()+": "+entry.getValue()+" "; }
            return String.format("%s\n%s\nGender Roles: %s\nGenres: %s\nCriteria: %s\nManaging Recruiter: %s\nCreated At: %s",
                getTitle(), getDescription(), gendersString, genresString, criteriaString, TalentRecruiter.getById(getRecruiterId()).toString(), getCreatedAt());
    
        } catch(IllegalArgumentException e) {
            System.err.println("Error getting Listing.toString(): "+e.getMessage());
        } catch(NullPointerException e) {
            System.err.println("Null ptr exception (likely from TR Id which is: "+getRecruiterId()+"): "+e.getMessage());
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}

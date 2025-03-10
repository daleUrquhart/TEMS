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
import com.tems.util.Env;

/**
 * Application model
 * @author Dale Urquhart
 */
public class Application {
    private final int APPLICATION_ID;
    private final int AUDITIONEE_ID;
    private final int LISTING_ID;
    private final Timestamp CREATED_AT;
    private String status;
    private final String RESUME;
    private final String COVER_LETTER;
    private int score;

    public Application(int applicationId, int auditionee_id, int listing_id, Timestamp submission_date, String status, String resume, String cover_letter) {
        this.APPLICATION_ID = applicationId;
        this.AUDITIONEE_ID = auditionee_id;
        this.LISTING_ID = listing_id;
        this.CREATED_AT = submission_date;
        this.status = status;
        this.RESUME = resume;
        this.COVER_LETTER = cover_letter;
    }

    // Getters and setters
    public int getApplicationId() { return APPLICATION_ID; }
    public int getAuditioneeId() { return AUDITIONEE_ID; }
    public int getListingId() { return LISTING_ID; }
    public Timestamp getSubmissionDate() { return CREATED_AT; }
    public String getStatus() { return status; }
    public String getResume() { return RESUME; }
    public String getCoverLetter() { return COVER_LETTER; }
    public int getFinalScore() { return score; }

    public void setFinalScore(List<Criteria> criteria, int[] scores) throws SQLException { 
        int sum = 0;
        int denom = 0;
        int weight;
        try {
            for(int i = 0; i < scores.length; i++) {
                int s = scores[i];
                Criteria c = criteria.get(i);
                weight = Criteria.getByListingAndTypeId(getListingId(), c.getCriteriaTypeId()).getWeight();
                sum += s * weight;
                denom += weight;
            } 
            this.score = sum / denom;  
            update(); 
        } catch(SQLException e) {
            throw new SQLException("Error calculating final score for app. id: "+getApplicationId()+"\n\t"+e.getMessage());
        }
    }

    public void setStatus(String status) { this.status = status; }

    // CRUD operations
    public static int create(int auditionee_id, int listingId, String resume, String coverLetter) throws SQLException{
        String sql = String.format("INSERT INTO Applications (%s, %s, %s, %s) VALUES (?, ?, ?, ?)", Env.AUDITIONEE_ID, Env.LISTING_ID, Env.RESUME, Env.COVER_LETTER);
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, auditionee_id);
            stmt.setInt(2, listingId);
            stmt.setString(3, resume);
            stmt.setString(4, coverLetter); 
            if(stmt.executeUpdate() == 0) throw new SQLException("No changes made to Applications after insert.");
            int applicationId;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) applicationId = rs.getInt(1);
                else throw new SQLException("No generated key returned.");
            }

            try {
                for(Map.Entry<CriteriaType, Integer> entry : Criteria.getByListingId(listingId).entrySet()) {
                    Score.create(applicationId, entry.getKey().getId());
                }

                return applicationId;
            } catch (SQLException e) {
                Application.delete(applicationId);
                throw new SQLException("Error creating scores for applciation");
            }
        } catch(SQLException e) {
            throw new SQLException("Error creating application: "+e.getMessage());
        } 
    } 

    private void update() throws SQLException {
        String sql = String.format("UPDATE Applications SET %s = ?, final_score = ? WHERE %s = ?", Env.STATUS, Env.APPLICATION_ID);
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, getStatus());
            stmt.setInt(2, getFinalScore());
            stmt.setInt(3, getApplicationId());
            if(stmt.executeUpdate() == 0) throw new SQLException("No rows changed for updating Application with ID "+getApplicationId()) ;

        } catch(SQLException e) {
            throw new SQLException("Error updating applicaiton: "+e.getMessage());
        }
    }

    public static ArrayList<Application> getByAudId(int auditioneeId) {
        String sql = String.format("SELECT * FROM Applications WHERE %s = ?", Env.AUDITIONEE_ID);
        ArrayList<Application> applications = new ArrayList<>();
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, auditioneeId);
            
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) applications.add( new Application(rs.getInt(Env.APPLICATION_ID), 
                auditioneeId, rs.getInt(Env.LISTING_ID), rs.getTimestamp(Env.CREATED_AT), 
                rs.getString(Env.STATUS), rs.getString(Env.RESUME), rs.getString(Env.COVER_LETTER)));
            }
        } catch(SQLException e) {
            System.err.println("Error searching applicaiton by auditionee id: "+e.getMessage());
        } 
        return applications;
    }

    public static ArrayList<Application> getByListingId(int listingId) {
        String sql = String.format("SELECT * FROM Applications WHERE %s = ?", Env.LISTING_ID);
        ArrayList<Application> applications = new ArrayList<>();
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listingId);
            
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) applications.add( new Application(rs.getInt(Env.APPLICATION_ID), 
                rs.getInt(Env.AUDITIONEE_ID), rs.getInt(Env.LISTING_ID), rs.getTimestamp(Env.CREATED_AT), 
                rs.getString(Env.STATUS), rs.getString(Env.RESUME), rs.getString(Env.COVER_LETTER)));
            }
        } catch(SQLException e) {
            System.err.println("Error searching applicaiton by listing id: "+e.getMessage());
        } 
        return applications;
    }

    public static void decline(int auditioneeId, int listingId) throws SQLException{        
        try {
            Application a = Application.getById(auditioneeId, listingId);
            a.setStatus("rejected");
            a.update();           
            Notification.create(auditioneeId, "Sorry, you were rejected for the role:\n"+Listing.getById(listingId).toString()); 
        } catch (SQLException e) {
            throw new SQLException("Error declining application: " + e.getMessage());
        }
    }
    
    public static void accept(int auditioneeId, int listingId) throws SQLException{        
        try {
            Application a = Application.getById(auditioneeId, listingId);
            a.setStatus("accepted");
            a.update();   
            Notification.create(auditioneeId, "Congratulations, you were selected for the role:\n"+Listing.getById(listingId).toString());
            
        } catch (SQLException e) {
            throw new SQLException("Error accepting application: " + e.getMessage());
        }
    }

    public static Application getById(int application_id) {
        String sql = "SELECT * FROM Applications WHERE application_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, application_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Application(
                            rs.getInt("application_id"),
                            rs.getInt("auditionee_id"),
                            rs.getInt("listing_id"),
                            rs.getTimestamp("created_at"),
                            rs.getString("status"),
                            rs.getString("resume"),
                            rs.getString("cover_letter")
                    );
                } else {
                    throw new SQLException("No listing found with id: " + application_id);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching listing by id: " + e.getMessage()); 
            return null;
        }

    }
 
    public static Application getById(int auditioneeId, int listingId) {
        String sql = "SELECT * FROM Applications WHERE listing_id = ? AND auditionee_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listingId);
            stmt.setInt(2, auditioneeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Application(
                            rs.getInt("application_id"),
                            rs.getInt("auditionee_id"),
                            rs.getInt("listing_id"),
                            rs.getTimestamp("created_at"),
                            rs.getString("status"),
                            rs.getString("resume"),
                            rs.getString("cover_letter")
                    );
                } else {
                    throw new SQLException("No listing found with listing id: " + listingId + ", and auditionee id: " + auditioneeId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching listing by id: " + e.getMessage()); 
            return null;
        }

    }
  
    public static void delete(int applicationId) throws SQLException{
        String sql = "DELETE FROM Applications WHERE application_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationId); 

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) throw new SQLException("No rows changed on deleting row with application id " + applicationId);

        } catch (SQLException e) {
            throw new SQLException("Error deleting applicaiton: \n\t" + e.getMessage());
        }
    }

    @Override 
    public String toString() {
        return String.format("Status: %s\nFinal Score: %s\nListing ID: %d\nAuditionee ID:%d\nResume:\n%s\nCoverLetter:\n%s\n", getStatus(), getFinalScore() == 0 ? "Not yet evaluated" : getFinalScore(), getListingId(), getAuditioneeId(), getResume(), getCoverLetter());
    }
}

package com.tems.models; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
 
import com.tems.util.ConnectionManager;
import com.tems.util.Env;

/**
 * Application model
 * @author Dale Urquhart
 */
public class Application {
    private final int applicationId;
    private final int auditioneeId;
    private final int listing_id;
    private final Timestamp submission_date;
    private String status;
    private final String resume;
    private final String coverLetter;
    private int score;

    public Application(int applicationId, int auditionee_id, int listing_id, Timestamp submission_date, String status, String resume, String cover_letter) {
        this.applicationId = applicationId;
        this.auditioneeId = auditionee_id;
        this.listing_id = listing_id;
        this.submission_date = submission_date;
        this.status = status;
        this.resume = resume;
        this.coverLetter = cover_letter;
    }

    // Getters and setters
    public int getApplicationId() { return applicationId; }
    public int getAuditioneeId() { return auditioneeId; }
    public int getListingId() { return listing_id; }
    public Timestamp getSubmissionDate() { return submission_date; }
    public String getStatus() { return status; }
    public String getResume() { return resume; }
    public String getCoverLetter() { return coverLetter; }
    public int getFinalScore() { return score; }

    public void setFinalScore() throws SQLException { 
        int sum = 0;
        int denom = 0;
        int weight;
        try {
            ArrayList<Score> scores = Score.getByAppId(getApplicationId());
            for(Score s : scores) {
                weight = Criteria.getByListingAndTypeId(getListingId(), s.getCriteriaId()).getWeight();
                sum += s.getScore() * weight;
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
                for(Criteria c : Criteria.getByListingId(listingId)) {
                    Score.create(applicationId, c.getCriteriaTypeId());
                }

                return applicationId;
            } catch (Exception e) {
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

    public static Application getById(int id) {
        String sql = "SELECT * FROM Applications WHERE application_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

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
                    throw new SQLException("No listing found with id: " + id);
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
 
    public void score(int criteriaId, int score) throws SQLException {
        ArrayList<Criteria> criteria = Criteria.getByListingId(getListingId());
        for(Criteria c : criteria) {
            if(c.getCriteriaTypeId() == criteriaId) {
                try {
                    Score.setScore(getApplicationId(), criteriaId, score);
                    return;
                } catch(SQLException e) {
                    throw new SQLException("Error scoring criteria: \n\t"+e.getMessage());
                }
            }
        }
        throw new SQLException("Criteria Type Id "+criteriaId+" not found for listing " + getListingId());
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
        return String.format("Status: %s\nListing ID: %d\nAuditionee ID:%d\nResume:\n%s\nCoverLetter:\n%s\n", getStatus(), getListingId(), getAuditioneeId(), getResume(), getCoverLetter());
    }
}

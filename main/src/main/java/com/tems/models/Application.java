package com.tems.models; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public void setStatus(String status) { this.status = status; }

    // CRUD operations
    public static boolean create(int auditionee_id, int listingId, String resume, String coverLetter) {
        String sql = String.format("INSERT INTO Applications (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)", Env.AUDITIONEE_ID, Env.LISTING_ID, Env.RESUME, Env.COVER_LETTER, Env.STATUS);
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, auditionee_id);
            stmt.setInt(2, listingId);
            stmt.setString(3, resume);
            stmt.setString(4, coverLetter); 
            return stmt.executeUpdate() > 0;

        } catch(SQLException e) {
            System.err.println("Error creating applicaiton: "+e.getMessage());
        }
        return false;
    } 

    public boolean update() {
        String sql = String.format("UPDATE Applications SET %s = ? WHERE %s = ?", Env.STATUS, Env.APPLICATION_ID);
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, getStatus());
            stmt.setInt(2, getApplicationId());
            return stmt.executeUpdate() > 0;

        } catch(SQLException e) {
            System.err.println("Error updating applicaiton: "+e.getMessage());
        }
        return false;
    }

    public static ArrayList<Application> getApplicationsByAudId(int auditioneeId) {
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

    public static ArrayList<Application> getApplicationsByListingId(int listingId) {
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

    public static boolean decline(int auditioneeId, int listingId) {
        String sql = String.format("UPDATE Applications SET %s = ? WHERE %s = ? AND %s = ?", Env.STATUS, Env.AUDITIONEE_ID, Env.LISTING_ID);
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, Env.DECLINED); 
            stmt.setInt(2, auditioneeId);
            stmt.setInt(3, listingId);
    
            return stmt.executeUpdate() > 0;
    
        } catch (SQLException e) {
            System.err.println("Error declining application: " + e.getMessage());
            return false;
        }
    }
    

    /**
     * Sum of scores for each criteria associated with the listing
     * @param applicationId
     * @return
     */
    public int getScore() {
        return -1;
    }
}

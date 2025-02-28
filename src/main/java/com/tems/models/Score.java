package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.tems.util.ConnectionManager;

public class Score {
    private final int APPLICATION_ID;
    private final int CRITERIA_ID;
    private final int SCORE;

    public Score(int APPLICATION_ID, int criteriaId, int score) {
        this.APPLICATION_ID = APPLICATION_ID;
        this.CRITERIA_ID = criteriaId;
        this.SCORE = score;
    }

    public int getApplicationId() { return APPLICATION_ID; }
    public int getCriteriaId() { return CRITERIA_ID; } 
    public int getScore() { return SCORE; } 

    //CRUD operations
    public static void create(int applicationId, int criteriaId) {
        String sql = "INSERT INTO Scores (application_id, criteria_id) VALUES (?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationId);
            stmt.setInt(2, criteriaId); 

            if(stmt.executeUpdate() == 0) throw new SQLException("No rows updated for creating score with application id " + applicationId + " and criteria id of "+criteriaId);

        } catch (SQLException e) {
            System.err.println("Error creating notification: " + e.getMessage());
        } 
    }

    public static void delete(int applicationId, int criteriaId) throws SQLException{
        String sql = "DELETE FROM Scores WHERE application_id = ? AND criteria_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationId);
            stmt.setInt(2, criteriaId);

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) throw new SQLException("No rows changed on deleting row with application id " + applicationId + " and criteria id of "+criteriaId);

        } catch (SQLException e) {
            throw new SQLException("Error deleting score: " + e.getMessage());
        }
    }

    public static void setScore(int applicationId, int criteriaId, int score) throws SQLException{
        String sql = "UPDATE Scores SET score = ? WHERE application_id = ? AND criteria_id = ?";
    
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, score); 
            stmt.setInt(2, applicationId); 
            stmt.setInt(3, criteriaId); 
            
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) throw new SQLException("No rows updated for updating score on app. id " + applicationId + " and criteria id of " + criteriaId);
        } catch (SQLException e) {
            throw new SQLException("Error updating score: " + e.getMessage());
        }
    }

    public static ArrayList<Score> getByAppId(int applicationId) throws SQLException{
        String sql = String.format("SELECT * FROM Scores WHERE application_id = ?");
        ArrayList<Score> scores = new ArrayList<>();
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) scores.add(new Score(rs.getInt("application_id"), rs.getInt("criteria_id"), rs.getInt("score")));
            }
        } catch(SQLException e) {
            throw new SQLException("Error searching scores by application id: "+e.getMessage());
        } 
        return scores;
    }

}

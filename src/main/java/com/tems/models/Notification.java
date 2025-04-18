package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.tems.util.ConnectionManager;

public class Notification {
    
    private final int NOTIFICATION_ID;
    private final int USER_ID;
    private final String MESSAGE;
    private boolean isRead;
    private final Timestamp CREATED_AT;

    public Notification(int notificationId, int userId, String message, boolean isRead, Timestamp createdAt) {
        this.NOTIFICATION_ID = notificationId;
        this.USER_ID = userId;
        this.MESSAGE = message;
        this.isRead = isRead;
        this.CREATED_AT = createdAt;
    }

    // Getters and setters
    public int getNotificaitonId() { return NOTIFICATION_ID; }
    public int getUserId() { return USER_ID; }
    public String getMessage() { return MESSAGE; }
    public boolean getIsRead() { return isRead; }
    public Timestamp getCreatedAt() { return CREATED_AT; }

    public void setIsRead(boolean isRead) { this.isRead = isRead; }
    
    // CRUD Operations
    public static void create(int userId, String message) throws SQLException { 
        String sql = "INSERT INTO Notifications (user_id, message) VALUES (?, ?)";
    
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) { 
    
            stmt.setInt(1, userId);
            stmt.setString(2, message);
    
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) throw new SQLException("Creating notification failed, no rows affected.");
    
        } catch (SQLException e) {
            throw new SQLException("Error creating notification: \n\t" + e.getMessage());
        }
    }    

    public static ArrayList<Notification> getByUserId(int id) throws SQLException{
        String sql = "SELECT * FROM Notifications WHERE user_id = ?";
        ArrayList<Notification> notifs = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { 
                    notifs.add(new Notification(rs.getInt("notification_id"), rs.getInt("user_id"), rs.getString("message"), rs.getBoolean("is_read"), rs.getTimestamp("created_at"))); 
                } 
            }
            return notifs;
        } catch (SQLException e) {
            throw new SQLException("Error fetching gender by name: " + e.getMessage());
        }
    }

    /**
     * Deletes a notification
     * @throws SQLException
     */
    public void delete() throws SQLException{
        String sql = "DELETE FROM Notifications WHERE notification_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, this.NOTIFICATION_ID);

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) throw new SQLException("Deleting notification failed, no rows affected.");

        } catch (SQLException e) {
            throw new SQLException("Error deleting notification: \n\t" + e.getMessage());
        }
    }

    public void update() throws SQLException {
        String sql = "UPDATE Notifications SET is_read = ? WHERE notification_id = ?";
    
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setBoolean(1, getIsRead()); 
            stmt.setInt(1, getNotificaitonId()); 
            
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) throw new SQLException("Updating notification failed, no rows affected.");
    
        } catch (SQLException e) {
            throw new SQLException("Error updating notification: \n\t" + e.getMessage());
        }
    } 

    @Override
    public String toString() {
        return "User: "+getUserId()+"\nMessage: "+getMessage();
    }
}

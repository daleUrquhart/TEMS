package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

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
        String sql = "INSERT INTO Notification (user_id, message) VALUES (?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, message); 

            if(stmt.executeUpdate() == 0) throw new SQLException ("Creating notification failed, no rows affected.");

        } catch (SQLException e) {
            throw new SQLException("Error creating notification: \n\t" + e.getMessage());
        }
    }

    public static void delete(int notificationId) throws SQLException{
        String sql = "DELETE FROM Notification WHERE notification_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notificationId);

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) throw new SQLException("Deleting notification failed, no rows affected.");

        } catch (SQLException e) {
            throw new SQLException("Error deleting notification: \n\t" + e.getMessage());
        }
    }

    public void update() throws SQLException {
        String sql = "UPDATE Notification SET is_read = ? WHERE notification_id = ?";
    
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

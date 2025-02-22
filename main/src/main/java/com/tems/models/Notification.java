package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.tems.util.ConnectionManager;

public class Notification {
    
    private final int notificationId;
    private final int userId;
    private final String message;
    private boolean isRead;
    private final Timestamp createdAt;

    public Notification(int notificationId, int userId, String message, boolean isRead, Timestamp createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getNotificaitonId() { return notificationId; }
    public int getUserId() { return userId; }
    public String getMessage() { return message; }
    public boolean getIsRead() { return isRead; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setIsRead(boolean isRead) { this.isRead = isRead; }
    
    // CRUD Operations
    public static boolean create(int userId, String message) { 
        String sql = "INSERT INTO Notification (user_id, message) VALUES (?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, message); 

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating notification: " + e.getMessage());
        }
        return false;
    }

    public static boolean delete(int notificationId) {
        String sql = "DELETE FROM Notification WHERE notification_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notificationId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            return false;
        }
    }

    public boolean update() {
        String sql = "UPDATE Notification SET is_read = ? WHERE notification_id = ?";
    
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setBoolean(1, getIsRead()); 
            stmt.setInt(1, getNotificaitonId()); 
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
    
        } catch (SQLException e) {
            System.err.println("Error updating notification: " + e.getMessage());
            return false;
        }
    }
}

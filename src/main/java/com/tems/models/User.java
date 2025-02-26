package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.tems.util.ConnectionManager;

/**
 * User model
 * @author Dale Urquhart
 */
public class User {
    private final int userId;
    private String name;
    private String email;
    private String passwordHash;
    private String role;

    // Constructor
    public User(int userId, String name, String email, String passwordHash, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }

    public static void deleteAllUsers() {
        String sql = "DELETE FROM Users";  
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {  
             
            stmt.execute(sql); 
            
        } catch (SQLException e) {
            System.err.println("Error resetting Users: " + e.getMessage());

        }
    }
    

    /**
     * Inserts a new user into the database.
     */
    public static int create(String name, String email, String passwordHash, String role) {
        String sql = "INSERT INTO Users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, role);

            if(stmt.executeUpdate() > 0) return User.getUserByEmail(email).getUserId();

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
        return -1;
    }
 
    /**
     * Finds a user by email.
     * @param email Email to search by
     * @return User
     * @throws SQLException if no user is found or there is a database issue.
     */
    public static User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Users WHERE email = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role")
                    );
                } else {
                    throw new SQLException("No user found with email: " + email);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by email: " + e.getMessage());
            throw e; 
        }
    }

    /**
     * Finds a user by email.
     * @param email Email to search by
     * @return User
     * @throws SQLException if no user is found or there is a database issue.
     */
    public static User getById(int id) throws SQLException {
        String sql = "SELECT * FROM Users WHERE user_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role")
                    );
                } else {
                    throw new SQLException("No user found with id: " + id);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by id: " + e.getMessage());
            throw e; 
        }
    }

    public static User signIn(String email, String passwordHash) throws SQLException {
        String sql = "SELECT * FROM Users WHERE email = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getString("password_hash").equals(passwordHash)) {
                        return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role")
                        );
                    } else {
                        throw new SQLException("Incorrect password for user with email: " + email);
                    } 
                } else {
                    throw new SQLException("No user found with email: " + email);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching user by email: "+ e.getMessage()); 
        }
    }

    /**
     * Updates the user's information in the database based on the instance data.
     */
    public boolean update() {
        String sql = "UPDATE Users SET name = ?, email = ?, password_hash = ?, role = ? WHERE user_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, role);
            stmt.setInt(5, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a user by ID.
     */
    public static boolean delete(int userId) {
        String sql = "DELETE FROM Users WHERE user_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * String repsentation of the user
     */
    @Override
    public String toString() {
        return String.format("id: %d\nname: %s\nemail: %s\npassword hash: %s\nrole: %s", getUserId(), getName(), getEmail(), getPasswordHash(), getRole());
    }
}

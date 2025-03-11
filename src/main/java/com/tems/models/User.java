package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.tems.util.ConnectionManager;
import com.tems.util.PasswordManager;

/**
 * User model
 * @author Dale Urquhart
 */
public class User {
    private final int USER_ID;
    private String name;
    private String email;
    private String passwordHash;
    private String role;

    // Constructor
    public User(int userId, String name, String email, String passwordHash, String role) {
        this.USER_ID = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters and Setters
    public int getUserId() { return USER_ID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }

    public static void deleteAllUsers() throws SQLException{
        String sql = "DELETE FROM Users";  
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {  
             
            stmt.execute(sql); 
            
        } catch (SQLException e) {
            throw new SQLException("Error resetting Users: \n\t" + e.getMessage());
        }
    }

    /**
     * Inserts a new user to the database and returns the new user's ID
     * @param name
     * @param email
     * @param passwordHash
     * @param role
     * @return
     * @throws SQLException
     */
    public static int create(String name, String email, String passwordHash, String role) throws SQLException{
        String sql = "INSERT INTO Users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, role);

            if(stmt.executeUpdate() > 0) return User.getUserByEmail(email).getUserId();
            else throw new SQLException("No rows updated after creating user.");

        } catch (SQLException e) {
            throw new SQLException("Error creating user: \n\t" + e.getMessage());
        }
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
                    if (PasswordManager.verifyPassword(passwordHash, rs.getString("password_hash"))) {
                        return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role")
                        );
                    } else {
                        throw new SQLException("Incorrect password for user with email: \n" + email + "(passsword hash = " + passwordHash + ")");
                    } 
                } else {
                    throw new SQLException("No user found with email: \n" + email);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching user by email: \n"+ e.getMessage()); 
        }
    }

    /**
     * Updates the user's information in the database based on the instance data.
     */
    public void update() throws SQLException{
        String sql = "UPDATE Users SET name = ?, email = ?, password_hash = ?, role = ? WHERE user_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, getName());
            stmt.setString(2, getEmail());
            stmt.setString(3, getPasswordHash());
            stmt.setString(4, getRole());
            stmt.setInt(5, getUserId());

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) throw new SQLException("No rows updated for updating user with id " + getUserId());

        } catch (SQLException e) {
            throw new SQLException("Error updating user: " + e.getMessage());
        }
    }

    /**
     * Deletes a user by ID.
     */
    public static void delete(int userId) throws SQLException{
        String sql = "DELETE FROM Users WHERE user_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) throw new SQLException("No rows changed on deleting user with id " + userId);

        } catch (SQLException e) {
            throw new SQLException("Error deleting user: " + e.getMessage());
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

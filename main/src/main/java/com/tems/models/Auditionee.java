package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tems.util.ConnectionManager;

/**
 * Manages an Auditionee with database integration.
 * @author Dale Urquhart
 */
public class Auditionee extends User {

    private int yoe;
    private Gender gender;

    public Auditionee(int id, String name, String email, String passwordHash, String role, Gender gender, int yoe) {
        super(id, name, email, passwordHash, role);
        this.yoe = yoe;
        this.gender = gender;
    }

    //Getters and setters
    public Gender getGender() { return gender; }
    public int getYOE() { return yoe; }

    public void setGender(Gender gender) { this.gender = gender; }
    public void setYOE(int yoe) { this.yoe = yoe; }

    @Override 
    public boolean update() {
        if (!super.update()) return false;

        String sql = "UPDATE Auditionees SET gender_id = ?, yoe = ? WHERE auditionee_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, getGender().getId());        
            stmt.setInt(2, getYOE());
            stmt.setInt(3, getUserId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating auditionee: " + e.getMessage());
            return false;
        }
    }
 
    public static int create(String name, String email, String passwordHash, Gender gender, int yoe) {
        int userCreated = User.create(name, email, passwordHash, "auditionee");
    
        if (userCreated > 0) {
            String sql = "INSERT INTO Auditionees (auditionee_id, gender_id, years_of_experience) VALUES (?, ?, ?)";
    
            try (Connection conn = ConnectionManager.getConnection()) {
                User user = getUserByEmail(email); 
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, user.getUserId());
                    stmt.setInt(2, gender.getId());
                    stmt.setInt(3, yoe);
                    if (stmt.executeUpdate() > 0) {
                        return userCreated;
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error creating auditionee: " + e.getMessage());
                User.delete(userCreated);
            }
        } 
        return -1;  
    }

    public static Auditionee getById(int id) throws SQLException{ 
        String sql = "SELECT * FROM Users WHERE user_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { //int id, String name, String email, String passwordHash, String role, Gender gender, int yoe
                    return new Auditionee(
                            rs.getInt("auditionee_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role"),
                            Gender.fromString(rs.getString("gender")),
                            rs.getInt("years_of_experience")
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
}

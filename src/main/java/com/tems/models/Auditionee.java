package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.tems.util.ConnectionManager;

/**
 * Manages an Auditionee with database integration.
 * @author Dale Urquhart
 */
public class Auditionee extends User {

    private int yoe;
    private Gender gender;

    public Auditionee(int id, String name, String email, String passwordHash, Gender gender, int yoe) {
        super(id, name, email, passwordHash, "auditionee");
        this.yoe = yoe;
        this.gender = gender;
    }

    //Getters and setters
    public Gender getGender() { return gender; }
    public int getYOE() { return yoe; }

    public void setGender(Gender gender) { this.gender = gender; }
    public void setYOE(int yoe) { this.yoe = yoe; }

    @Override 
    public void update() throws SQLException{
        String sql = "UPDATE Auditionees SET gender_id = ?, yoe = ? WHERE auditionee_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            super.update();

            stmt.setInt(1, getGender().getId());        
            stmt.setInt(2, getYOE());
            stmt.setInt(3, getUserId());

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) throw new SQLException("No rows updated for updating auditionee with id " + getUserId());
        } catch (SQLException e) {
            throw new SQLException("Error updating auditionee: \n\t" + e.getMessage());
        }
    }
 
    public ArrayList<Gender> getGenderRoles() throws SQLException {
        String sql = "SELECT * FROM AuditioneeGenderRoles WHERE auditionee_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ArrayList<Gender> genders = new ArrayList<>();
            stmt.setInt(1, getUserId());
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                genders.add(Gender.getById(rs.getInt("gender_id")));
            } 
            return genders; 
        } catch (SQLException e) {
            throw new SQLException("Error fetching AuditioneeGenderRoles by auditionee id: \n\t" + e.getMessage()); 
        } 
    }

    public static int create(String name, String email, String passwordHash, Gender gender, int yoe) throws SQLException{
        try {
            int userCreated = User.create(name, email, passwordHash, "auditionee");
            String sql = "INSERT INTO Auditionees (auditionee_id, gender_id, years_of_experience) VALUES (?, ?, ?)";
    
            try (Connection conn = ConnectionManager.getConnection()) {
                User user = getUserByEmail(email); 
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, user.getUserId());
                    stmt.setInt(2, gender.getId());
                    stmt.setInt(3, yoe);
                    if (stmt.executeUpdate() > 0) {
                        return userCreated;
                    } else throw new SQLException("No rows updated after creating auditionee.");
                } catch (SQLException e) { 
                    throw new SQLException("Error creating auditionee: \n\t" + e.getMessage());
                }
            } catch (SQLException e) {
                User.delete(userCreated);
                throw new SQLException("Error with database operation for creating auditionee: \n\t" + e.getMessage());
            }
        } catch (SQLException e) {
            throw new SQLException("Error creating user: \n\t" + e.getMessage());
        }
    }

    public static Auditionee getById(int id) throws SQLException{ 
        try {
            // Get the user of id: id and make sure it is an auditionee
            User u = User.getById(id);
            if(!u.getRole().equals("auditionee")) throw new SQLException(String.format("User with id: %d is not an auditionee.", id));
            
            // Try getting the auditionee with id: id
            String sql = "SELECT * FROM Auditionees WHERE auditionee_id = ?";
            try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) { //int id, String name, String email, String passwordHash, String role, Gender gender, int yoe
                        return new Auditionee(
                            rs.getInt("auditionee_id"),
                            u.getName(),
                            u.getEmail(),
                            u.getPasswordHash(),
                            Gender.getById(rs.getInt("gender_id")),
                            rs.getInt("years_of_experience")
                        );
                    } else throw new SQLException("No auditionee found with id: " + id);
                } catch(SQLException e) {
                    throw new SQLException("Error fetching auditionee by id: \n\t" + e.getMessage());
                }
            } catch (SQLException e) { 
                throw new SQLException("Error establishing connection: \n\t" + e.getMessage()); 
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching user by id: \n\t" + e.getMessage()); 
        } 
    }
}

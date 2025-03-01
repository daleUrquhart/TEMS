package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tems.util.ConnectionManager;

/**
 * Manages an Auditionee with database integration.
 * @author Dale Urquhart
 */
public class Auditionee extends User {

    private int yoe;
    private Gender gender;
    private List<Gender> genderRoles;

    public Auditionee(int id, String name, String email, String passwordHash, Gender gender, int yoe, List<Gender> genderRoles) {
        super(id, name, email, passwordHash, "auditionee");
        this.yoe = yoe;
        this.gender = gender;
        this.genderRoles = genderRoles;
    }

    //Getters and setters
    public Gender getGender() { return gender; }
    public int getYOE() { return yoe; }

    public void setGender(Gender gender) { this.gender = gender; }
    public void setYOE(int yoe) { this.yoe = yoe; }
    public void setGenderRoles(List<Gender> genderRoles) { this.genderRoles = genderRoles; }
    
    @Override
    public void update() throws SQLException {
        String sqlAuditionee = "UPDATE Auditionees SET gender_id = ?, years_of_experience = ? WHERE auditionee_id = ?";
        String deleteRoles = "DELETE FROM AuditioneeGenderRoles WHERE auditionee_id = ?";
        String insertRoles = "INSERT INTO AuditioneeGenderRoles (auditionee_id, gender_id) VALUES (?, ?)";

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false); 

            // Update Auditionee table
            try (PreparedStatement stmt = conn.prepareStatement(sqlAuditionee)) {
                super.update();
                stmt.setInt(1, getGender().getId());
                stmt.setInt(2, getYOE());
                stmt.setInt(3, getUserId());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    throw new SQLException("No rows updated for auditionee with ID " + getUserId());
                }
            }

            // Delete existing gender roles
            try (PreparedStatement stmtDelete = conn.prepareStatement(deleteRoles)) {
                stmtDelete.setInt(1, getUserId());
                stmtDelete.executeUpdate();
            }

            // Insert new gender roles
            try (PreparedStatement stmtInsert = conn.prepareStatement(insertRoles)) {
                for (Gender g : genderRoles) {
                    stmtInsert.setInt(1, getUserId());
                    stmtInsert.setInt(2, g.getId());
                    stmtInsert.addBatch();
                }
                stmtInsert.executeBatch();
            }

            conn.commit(); // Commit transaction
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

    public static ArrayList<Auditionee> getAll() throws SQLException {
        String sql = "SELECT * FROM Auditionees";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ArrayList<Auditionee> auditionees = new ArrayList<>();
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                auditionees.add(Auditionee.getById(rs.getInt("auditionee_id")));
            } 
            return auditionees; 
        } catch (SQLException e) {
            throw new SQLException("Error fetching auditionees: \n\t" + e.getMessage()); 
        } 
    }

    public static int create(String name, String email, String passwordHash, Gender gender, int yoe, List<Gender> genderRoles) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) { 
            conn.setAutoCommit(false);
    
            // Create User first
            User.create(name, email, passwordHash, "auditionee");
            User user = getUserByEmail(email);
            
            if (user == null) {
                conn.rollback();
                throw new SQLException("User creation failed, no user found with email: " + email);
            }
    
            int userId = user.getUserId(); 
    
            // Insert into Auditionees
            String sqlAuditionee = "INSERT INTO Auditionees (auditionee_id, gender_id, years_of_experience) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlAuditionee)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, gender.getId());
                stmt.setInt(3, yoe);
    
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    throw new SQLException("No rows updated after creating auditionee.");
                } 
            }
    
            // Insert into AuditioneeGenderRoles
            if (!genderRoles.isEmpty()) {
                String sqlGenderRoles = "INSERT INTO AuditioneeGenderRoles (auditionee_id, gender_id) VALUES (?, ?)";
                try (PreparedStatement stmtGender = conn.prepareStatement(sqlGenderRoles)) {
                    for (Gender g : genderRoles) {
                        stmtGender.setInt(1, userId);
                        stmtGender.setInt(2, g.getId());
                        stmtGender.addBatch();
                    }
                    stmtGender.executeBatch(); 
                }
            }
    
            // Commit transaction
            conn.commit(); 
            return userId;
    
        } catch (SQLException e) { 
            throw new SQLException("Error during auditionee creation: \n\t" + e.getMessage());
        }
    }
    
    public static Auditionee getById(int id) throws SQLException {
        String sqlAuditionee = "SELECT * FROM Auditionees WHERE auditionee_id = ?";
        String sqlGenderRoles = "SELECT gender_id FROM AuditioneeGenderRoles WHERE auditionee_id = ?";
    
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction
    
            // Step 1: Get the user and validate it's an auditionee
            User u = User.getById(id);
            if (!u.getRole().equals("auditionee")) {
                conn.rollback();
                throw new SQLException(String.format("User with id: %d is not an auditionee.", id));
            }
    
            Auditionee auditionee = null;
    
            // Step 2: Fetch auditionee data
            try (PreparedStatement stmt = conn.prepareStatement(sqlAuditionee)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        auditionee = new Auditionee(
                            rs.getInt("auditionee_id"),
                            u.getName(),
                            u.getEmail(),
                            u.getPasswordHash(),
                            Gender.getById(rs.getInt("gender_id")),
                            rs.getInt("years_of_experience"),
                            new ArrayList<>() // Placeholder for gender roles
                        );
                    } else {
                        conn.rollback();
                        throw new SQLException("No auditionee found with id: " + id);
                    }
                }
            }
    
            // Step 3: Fetch and set gender roles
            try (PreparedStatement stmt = conn.prepareStatement(sqlGenderRoles)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    List<Gender> genderRoles = new ArrayList<>();
                    while (rs.next()) {
                        genderRoles.add(Gender.getById(rs.getInt("gender_id")));
                    }
                    auditionee.setGenderRoles(genderRoles);
                }
            }
    
            conn.commit(); // Commit transaction
            return auditionee;
    
        } catch (SQLException e) {
            throw new SQLException("Error fetching auditionee by ID: \n\t" + e.getMessage());
        }
    }    
}

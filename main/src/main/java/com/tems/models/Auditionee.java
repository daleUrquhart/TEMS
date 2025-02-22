package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tems.util.ConnectionManager;

/**
 * Manages an Auditionee with database integration.
 * @author Dale Urquhart
 */
public class Auditionee extends User {

    public Auditionee(int id, String name, String email, String passwordHash, String role) {
        super(id, name, email, passwordHash, role);
    }

    public static int createAuditionee(String name, String email, String passwordHash, Gender gender, int yoe) {
        int userCreated = createUser(name, email, passwordHash, "auditionee");
    
        if (userCreated > 0) {
            String sql = "INSERT INTO Auditionees (auditionee_id, gender_id, years_of_experience) VALUES (?, ?, ?)";
    
            try (Connection conn = ConnectionManager.getConnection()) {
                User user = getUserByEmail(email); 
    
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, user.getUserId());
                    stmt.setInt(2, gender.getId());
                    stmt.setInt(3, yoe);
                    if(stmt.executeUpdate() > 0) return userCreated;
                }
            } catch (SQLException e) {
                System.err.println("Error creating auditionee: " + e.getMessage());
                User.deleteUser(userCreated);
            }
        } 
        return -1;  
    }
}

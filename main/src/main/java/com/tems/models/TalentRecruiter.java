package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tems.util.ConnectionManager;

/**
 * TalentRecruiter model
 * @author Dale Urquhart
 */
public class TalentRecruiter extends User{ 

    public TalentRecruiter(int trId, String name, String email, String passwordHash, String role) {
        super(trId, name, email, passwordHash, role); 
    } 

    public static int createTalentRecruiter(String name, String email, String passwordHash, String company) {
        int userCreated = createUser(name, email, passwordHash, "recruiter");
    
        if (userCreated > 0) {
            String sql = "INSERT INTO TalentRecruiters (recruiter_id, company) VALUES (?, ?)";
    
            try (Connection conn = ConnectionManager.getConnection()) {
                User user = getUserByEmail(email); 
    
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, user.getUserId());
                    stmt.setString(2, company);
    
                    if(stmt.executeUpdate() > 0) return userCreated;
                }
            } catch (SQLException e) {
                System.err.println("Error creating recruiter: " + e.getMessage());
                deleteUser(userCreated); 
            }
        } 
        return -1;  
    }    
}

package com.tems.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 

import com.tems.util.ConnectionManager;

/**
 * TalentRecruiter model
 * @author Dale Urquhart
 */
public class TalentRecruiter extends User{ 

    private final String company;
    public TalentRecruiter(int trId, String name, String email, String passwordHash, String role, String company) {
        super(trId, name, email, passwordHash, role); 
        this.company = company;
    } 

    public String getCompany() {
        return company;
    }

    @Override
    public boolean update() {
        if(!super.update()) return false;
        String sql = "UPDATE TalentRecruiters SET company = ? WHERE recruiter_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, company);  
            stmt.setInt(2, getUserId());  

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating recruiter: " + e.getMessage());
            return false;
        } 
    }
 
    public static int create(String name, String email, String passwordHash, String company) throws SQLException {
        try {
            int userCreated = User.create(name, email, passwordHash, "recruiter");
            String sql = "INSERT INTO TalentRecruiters (recruiter_id, company) VALUES (?, ?)";
    
            try (Connection conn = ConnectionManager.getConnection()) {
                User user = getUserByEmail(email); 
    
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, user.getUserId());
                    stmt.setString(2, company);
    
                    if(stmt.executeUpdate() > 0) return userCreated;
                    throw new SQLException("No rows affected upon creating recruiter");
                }
            } catch (SQLException e) {
                delete(userCreated); 
                throw new SQLException("Error creating recruiter: \n\t" + e.getMessage());
            }
        } catch (SQLException e) { 
            throw new SQLException("Error creating user: \n\t" + e.getMessage());
        }
    }   

    public static TalentRecruiter getById(int id) throws SQLException { 
        String sql = "SELECT * FROM TalentRecruiters WHERE recruiter_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User temp = User.getById(id);
                    return new TalentRecruiter(
                            rs.getInt("recruiter_id"),
                            temp.getName(),
                            temp.getEmail(),
                            temp.getPasswordHash(),
                            temp.getRole(),
                            rs.getString("company") 
                    );
                } else {
                    throw new SQLException("No recruiter found with id: " + id);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching recruiter by id: " + e.getMessage());
        }
    } 

    @Override
    public String toString() {
        return getName()+" from "+getCompany();
    }
}

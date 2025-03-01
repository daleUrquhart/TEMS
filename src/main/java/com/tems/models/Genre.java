package com.tems.models;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tems.util.ConnectionManager;

/**
 * Represents Genre options for users for data integrety and consistencey
 * @author Dale Urquhart
 */
public enum Genre {
    ACTION("Action"),
    DRAMA("Drama"),
    COMEDY("Comedy"),
    SCIFI("Sci-Fi"),
    HORROR("Horror"),
    ROMANCE("Romance"),
    OTHER("Other");
 
    private final String displayName;
 
    Genre(String displayName) {
        this.displayName = displayName;
    }
 
    public String getDisplayName() {
        return displayName;
    }
  
    public static Genre fromString(String text) throws IllegalArgumentException{
        for (Genre Genre : Genre.values()) {
            if (Genre.displayName.equalsIgnoreCase(text)) {
                return Genre;
            }
        }
        throw new IllegalArgumentException("No Genre found with name: " + text);
    }

    // CRUD Operations 
    public int getId() throws SQLException{
        String sql = "SELECT * FROM Genres WHERE genre_name = ?";
        String Genre_name = getDisplayName();

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Genre_name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("genre_id");
                } else {
                    throw new SQLException("No user found with name: " + Genre_name);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching Genre by name: " + e.getMessage());
            throw e; 
        }
    }

    public static ArrayList<Genre> getAll() throws SQLException {
        String sql = "SELECT * FROM Genres";
        ArrayList<Genre> genres = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) { 

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                genres.add(Genre.getById(rs.getInt("genre_id")));
            } 
            return genres;
        } catch (SQLException e) {
            throw new SQLException("Error fetching gender by name: " + e.getMessage());
        }
    }

    public static List<Genre> getByListingId(int listingId) {
        String sql = "SELECT * FROM ListingGenres WHERE listing_id = ?";
        ArrayList<Genre> genres = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, listingId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(Genre.getById(rs.getInt("genre_id"))); 
                }
            }
            return genres;
        } catch (SQLException e) {
            System.err.println("Error fetching genre for listing id " + listingId + ": " + e.getMessage());
        } 
        return null;
    } 

    public static Genre getById(int id) {
        String sql = "SELECT * FROM Genres WHERE genre_id = ?";
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return Genre.fromString(rs.getString("genre_name"));
                }
            }
        } catch(SQLException e) {
            System.err.println("Error getting genre by Id: "+e.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        return getDisplayName()+" ";
    }
}

package com.tems.models;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    /**
     * String representation of enum constant
     */
    private final String displayName;

    /**
     * Constructor assigns the string version of the enum
     * @param displayName Display name of the enum
     */
    Genre(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the Genres id of the Genre
     * @param Genre
     * @return
     */
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

    /**
     * Gets the display name of the enum 
     * @return Display name of the enum 
     */
    public String getDisplayName() {
        return displayName;
    }
 
    /**
     * Gets enum constant from string value
     * @param text String representation to find constant for
     * @return Enum constant of string value
     * @throws IllegalArgumentException For when String value has no asssociated constant
     */
    public static Genre fromString(String text) throws IllegalArgumentException{
        for (Genre Genre : Genre.values()) {
            if (Genre.displayName.equalsIgnoreCase(text)) {
                return Genre;
            }
        }
        throw new IllegalArgumentException("No Genre found with name: " + text);
    }
}

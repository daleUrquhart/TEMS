package com.tems.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordManager {

    // Hashes the password using BCrypt with a default salt (work factor of 12)
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Verifies that the provided password matches the hashed password
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}

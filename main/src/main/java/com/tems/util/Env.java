package com.tems.util;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    private static final Dotenv dotenv = Dotenv.load();

    // Database configuration
    public static final String DB_HOST = dotenv.get("DB_HOST");
    public static final String DB_PORT = dotenv.get("DB_PORT");
    public static final String DB_NAME = dotenv.get("DB_NAME");
    public static final String DB_USER = dotenv.get("DB_USER");
    public static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    // Common strings
    public static final String AUDITIONEE_ID = dotenv.get("AUDITIONEE_ID");
    public static final String APPLICATION_ID = dotenv.get("APPLICTION_ID"); 
    public static final String LISTING_ID = dotenv.get("LISTING_ID");
    public static final String CRITERIA_ID = dotenv.get("CRITERIA_ID");
    public static final String RESUME = dotenv.get("RESUME");
    public static final String COVER_LETTER = dotenv.get("COVER_LETTER");
    public static final String STATUS = dotenv.get("STATUS");
    public static final String CREATED_AT = dotenv.get("CREATED_AT");
}

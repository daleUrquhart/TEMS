package com.tems.models; 

import java.sql.Timestamp;

public class Application {
    private final int applicationId;
    private final int auditioneeId;
    private final int listing_id;
    private final Timestamp submission_date;
    private String status;
    private final String resume;
    private final String coverLetter;

    public Application(int applicationId, int auditionee_id, int listing_id, Timestamp submission_date, String status, String resume, String cover_letter) {
        this.applicationId = applicationId;
        this.auditioneeId = auditionee_id;
        this.listing_id = listing_id;
        this.submission_date = submission_date;
        this.status = status;
        this.resume = resume;
        this.coverLetter = cover_letter;
    }

    // Getters and setters
    public int getApplicationId() { return applicationId; }
    public int getAuditioneeId() { return auditioneeId; }
    public int getListingId() { return listing_id; }
    public Timestamp getSubmissionDate() { return submission_date; }
    public String getStatus() { return status; }
    public String getResume() { return resume; }
    public String getCoverLetter() { return coverLetter; }

    // CRUD operations
    public void setStatus(String status) { this.status = status; }

    public static boolean create(int auditionee_id, int listing_id, Timestamp submission_date, String status, String resume, String cover_letter) {
        
        return false;
    } 

    public boolean update() {
        return false;
    }

    public boolean getApplicationsByAudId() {
        return false;
    }

    public boolean getApplicationsByListingId() {
        return false;
    }

    public boolean decline() {
        return false;
    }

    /**
     * Sum of scores for each criteria associated with the listing
     * @param applicationId
     * @return
     */
    public int getScore() {
        return -1;
    }
}

package com.tems.models;

/**
 * Manages an Auditionee with database integration.
 * @author Dale Urquhart
 */
public class Auditionee extends User {

    public Auditionee(int id, String name, String email, String passwordHash, String role) {
        super(id, name, email, passwordHash, role);
    }
}

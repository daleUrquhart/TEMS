package com.tems;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract class hosting functionalities shared with TalentRecruiter and Auditionee
 * @author Dale Urquhart
 */
public abstract class User {

    /**
     * Name of the user
     */
    private String name;

    /**
     * Static ID value
     */
    private static final AtomicInteger idGenerator = new AtomicInteger(1000);

    /**
     * ID of the user
     */
    private final int ID;

    /**
     * Constructs a user
     * @param name Name of the user
     * @throws IllegalArgumentException If name is null or empty
     */
    public User(String name) throws IllegalArgumentException{
        validateName(name);
        this.name = name;
        this.ID = idGenerator.getAndIncrement();
    }
    
    /**
     * Gets the user's ID
     * @return the user's ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Gets the user's name
     * @return User's name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the user's name
     * @param newName User's new name
     * @throws IllegalArgumentException If name is null or empty
     */
    public void updateName(String newName) throws IllegalArgumentException {
        validateName(newName);
        name = newName;
    }

    /**
     * Validates a user's name
     * @param name Name to validate
     * @throws IllegalArgumentException If name is null or empty
     */
    private static void validateName(String name) throws IllegalArgumentException {
        if(name == null || name.trim().isEmpty()) throw new IllegalArgumentException(String.format("%s is an invalid name.", name));
    }

    /**
     * Returns a string representation of the user for debugging.
     * @return a string representation of the user
     */
    @Override
    public String toString() {
        return "User[ID=" + ID + ", Name=" + name + "]";
    }
}

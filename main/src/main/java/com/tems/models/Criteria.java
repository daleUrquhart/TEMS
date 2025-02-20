package com.tems.models;

/**
 * Manages criteria which may be needed for a Listing
 * @author Dale Urquhart
 */
public class Criteria {
    
    /**
     * Weight the criteria has on the listing application
     */
    private int weight;

    /**
     * Score range of the criteria
     */
    private final static int RANGE = 100;

    /**
     * Default weight value if not specified
     */
    private static final int DEFAULT_WEIGHT = 1;

    /**
     * Default constructor setting weight to its default value
     */
    public Criteria() {
        weight = DEFAULT_WEIGHT;
    }

    /**
     * Parametrized constructor
     * @param weight Weight of the criteria
     * @throws IllegalArgumentException If weight is < 0
     */
    public Criteria(int weight) throws IllegalArgumentException{
        validateWeight(weight);
        this.weight = weight;
    }

    /**
     * Gets the weight of the criteria
     * @return Weight of the criteria
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Gets the range of the criteria 
     * @return Range of the criteria
     */
    public int getRange() {
        return RANGE;
    }

    /**
     * Updates the weight of the criteria
     * @throws IllegalArgumentException If weight is < 0
     */
    public void updateWeight(int newWeight) throws IllegalArgumentException {
        validateWeight(newWeight);
        weight = newWeight;
    }

    /**
     * Validates the weight of the criteria
     * @param weight Weight to validate
     * @throws IllegalArgumentException If weight is < 0
     */
    private static void validateWeight(int weight) throws IllegalArgumentException {
        if(weight < 0) throw new IllegalArgumentException(String.format("%d is an invalid paramater for criteria weight.", weight));
    }

    /**
     * String representation of the criteria for debugging
     * @return String representation of the criteria
     */
    @Override 
    public String toString() {
        return String.format("Criteria[Weight=%d, Range=%d]", getWeight(), getRange());
    }
}

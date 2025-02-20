package com.tems.models;

import java.util.ArrayList;

/**
 * Abstract class for display pages
 * @author Dale Urquhart
 */
public abstract class Display<T extends Displayable> {

    /**
     * Items the page is displaying
     */
    ArrayList<T> items = new ArrayList<>();

    /**
     * Abstract constructor
     */
    public Display(ArrayList<T> items) {
        this.items = items;
        //TODO load page or whatever here in teh constructor ? or have a method to do it
    }

    /**
     * gets items 
     * @return Items
     */
    public ArrayList<T> getItems() {
        return items;
    }

    /**
     * Gets a string representation of the items collection
     * @return a string representation of the items collection
     */
    @Override
    public String toString() {
        String out = "";
        for(T t : items) {
            out += t.toString() + "\n";
        }
        return out;
    }
}

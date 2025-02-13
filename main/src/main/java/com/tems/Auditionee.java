package com.tems;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Manages an Auditionee
 * @author Dale Urquhart
 */
public class Auditionee extends User implements Displayable { 

    /**
     * Gender of the auditionee
     */
    private Gender gender;
    
    /**
     * Years of experience the auditionee has
     */
    private Integer yoe;

    /**
     * Constructs an auditionee
     * @param name Name of the auditionee
     * @param gender Gender of the auditionee
     * @param yoe Years of experience for the auditionee
     * @throws IllegalArgumentException for Invalid name, years of experience
     */
    public Auditionee(String name, Gender gender, Integer yoe) throws IllegalArgumentException {
        super(name);
        validateYOE(yoe);
        this.gender = gender;
        this.yoe = yoe;
    }

    /**
     * Changes teh auditionee's gender
     * @param newGender The new gender the auditionee is switching to
     */
    public void setGender(Gender newGender) {
        gender = newGender;
    }

    /**
     * Increments teh auditionee's years of experience
     */
    public void incrementYOE() {
        yoe++;
    }

    /**
     * Gets the auditionee's gender
     * @return Gender of the auditionee
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Gets the years of experience the auditionee has
     * @return Years of experience the auditionee has
     */
    public int getYOE() {
        return yoe;
    }

    /**
     * Validates an auditionee's years of experience
     * @throws IllegalArgumentException If YOE is < 0
     */
    private static void validateYOE(int yoe) throws IllegalArgumentException{
        if(yoe < 0) throw new IllegalArgumentException(String.format("%d is an invalid value for years of experience", yoe));
    }

    /**
     * Builds and returns a HBox display of teh auditionee
     * @return displayType (0 for TR view, 1 for Auditionee view)
     * @return HBox representing the auditionee with available actions
     * @throws IllegalArgumentException Invalid displayType entered (not 0 or 1)
     */
    @Override
    public HBox getDisplay(int displayType) throws IllegalArgumentException{
        HBox display = new HBox(10);

        switch (displayType) {
            case 0 -> { // TR View
                Label info = new Label(this.toString());

                Button offerPosition = new Button("Offer Position");
                offerPosition.setOnAction(event -> offer()); 
                
                Button delete = new Button("Delete");
                delete.setOnAction(event -> delete());

                display.getChildren().addAll(info, offerPosition, delete);
            }
            case 1 -> { // Auditionee View
                Label info = new Label(this.toString());

                Button changeGender = new Button("Change Gender");
                changeGender.setOnAction(event -> selectNewGender());

                Button changeName = new Button("Change Name");
                changeName.setOnAction(event -> selectNewName());

                Button addYOE = new Button("Increase Experience");
                addYOE.setOnAction(event -> incrementYOE());

                Button delete = new Button("Delete");
                delete.setOnAction(event -> delete());

                Button notifications = new Button("View Notifications");
                notifications.setOnAction(event -> viewNotifications());

                display.getChildren().addAll(info, changeGender, changeName, addYOE, delete, notifications);
            }
            default -> throw new IllegalArgumentException(String.format("%d display type paramater is invalid.", displayType));
        }
        return display;
    }

    /**
     * Views the users notifications
     */
    public void viewNotifications() {
        //TODO
    }

    /**
     * Prompts user to select a new name
     */
    public void selectNewName() {
        //TODO
    }

    /**
     * Prompts user to select new gender
     */
    public void selectNewGender() {
        //TODO
    }

    /**
     * Deletes the auditionee form the DB
     */
    public void delete() {
        //TODO
    }

    /**
     * Prompts an alert for TR to select an offer to send and will notify the auditionee of a listing that they may accept
     */
    public void offer() {
        //TODO
    }

    /**
     * Gets string representation of the auditionee
     * @return string repsentaiton of the auditionee
     */
    @Override 
    public String toString() {
        return String.format("Name: %s, Gender: %s, Years of experience: %d", getName(), getGender().toString(), getYOE());
    }
}

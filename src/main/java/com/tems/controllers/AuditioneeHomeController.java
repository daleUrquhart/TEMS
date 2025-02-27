package com.tems.controllers;

import java.sql.SQLException;

import com.tems.models.Auditionee;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@SuppressWarnings("unused") 
public class AuditioneeHomeController extends MainController {
    Auditionee auditionee; 

    @FXML
    private Label infoLabel; 

    @FXML
    private void viewListings() {

    }

    @FXML 
    private void viewApplications() {

    }

    @FXML
    private void viewNotifications() {

    }

    /**
     * Edit / delete user's profile
     */
    @FXML 
    private void editProfile() {

    }

    public void setUserData(int id) {
        try {
            this.auditionee = Auditionee.getById(id);
            infoLabel.setText(auditionee.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while loading the user data. \n"+e.getMessage());
        } 
    }
}

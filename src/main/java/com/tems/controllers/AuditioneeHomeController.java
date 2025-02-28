package com.tems.controllers;
 
import java.sql.SQLException;

import com.tems.models.Auditionee;

import javafx.fxml.FXML; 
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") 
public class AuditioneeHomeController implements BaseController {
    private Auditionee auditionee; 
    private MainController mainController; 

    @FXML 
    private VBox mainBox; 

    @FXML 
    private void viewApplications() {
        mainController.loadApplicationsView(auditionee.getUserId());
    }

    @FXML
    private void viewListings() {
        mainController.loadAudListingView(auditionee.getUserId());
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
        } catch (SQLException e) { 
            mainController.showErrorAlert("Error", "An error occurred while loading the user data. \n"+e.getMessage());
        } 
    } 



    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

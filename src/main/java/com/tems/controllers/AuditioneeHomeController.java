package com.tems.controllers;

import java.io.IOException;
import java.sql.SQLException;

import com.tems.models.Auditionee;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") 
public class AuditioneeHomeController implements BaseController {
    private Auditionee auditionee; 
    private MainController mainController; 

    @FXML 
    private VBox mainBox; 

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
        } catch (SQLException e) {
            e.printStackTrace();
            mainController.showErrorAlert("Error", "An error occurred while loading the user data. \n"+e.getMessage());
        } 
    }

    // In MainController or SignInController 
    @FXML
    private void viewListings() { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AuditioneeListingView.fxml"));
            VBox view = loader.load();

            // Set the loaded view into the center of the BorderPane
            BorderPane mainPane = (BorderPane) mainBox.getScene().getRoot();
            ListingController controller = loader.getController();
            controller.setUserData(auditionee);
            mainPane.setCenter(view);

        } catch (IOException e) { 
            e.printStackTrace();
            mainController.showErrorAlert("Error", "An error occurred while loading the view.\n\t"+e.getMessage());
        }
    }


    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

package com.tems.controllers;
 
import java.sql.SQLException;

import com.tems.models.Auditionee;

import javafx.fxml.FXML; 
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") 
public class AuditioneeHomeController implements BaseController { 
    private MainController mainController; 
    private int id;
    @FXML 
    private VBox mainBox; 

    @FXML 
    private void viewApplications() {
        mainController.loadApplicationsView(id);
    }

    @FXML
    private void viewListings() {mainController.loadAudListingView(id);}

    @FXML
    private void viewNotifications() { mainController.loadNotificationsView(id); }
 
    @FXML 
    private void editProfile() { mainController.loadEditProfileView(id); }

    public void setUserData(int id) {
        this.id = id;
    } 



    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

package com.tems.controllers;

import java.sql.SQLException;

import com.tems.models.User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@SuppressWarnings("unused")
public class HomeController implements BaseController{
    private MainController mainController;
    private int id;

    @FXML private Label infoLabel;

    //Admin
    @FXML private void viewRecruiters() { mainController.loadProfilesView(id, 'T'); }
    
    @FXML private void handleCreateTR() { mainController.loadTRCreateView(); }

    // Aud   
    @FXML private void viewApplications() { mainController.loadApplicationsView(id); } 

    // TR
    @FXML public void createListing() { mainController.loadListingView(id); }

    @FXML public void viewAuditionees() { mainController.loadProfilesView(id, 'A'); }
    
    // General
    @FXML public void viewListings() { mainController.loadListingsView(id, null); }

    @FXML private void viewNotifications() { mainController.loadNotificationsView(id); }

    @FXML public void editProfile() { mainController.loadEditProfileView(id); }

    public void setUserData(int id) {
        try {
            this.id = id;
            infoLabel.setText("Welcome, "+User.getById(id).getName());
        } catch(SQLException e) {
            mainController.showErrorAlert("Error", "Error loading user: \n\t"+e.getMessage());
        }
    } 

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
} 

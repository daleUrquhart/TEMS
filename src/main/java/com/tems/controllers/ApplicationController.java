package com.tems.controllers;

import java.sql.SQLException; 

import com.tems.models.Application; 
import com.tems.models.Auditionee;
import com.tems.models.Listing;
import com.tems.models.Notification;

import javafx.fxml.FXML;
import javafx.scene.control.TextField; 

@SuppressWarnings("unused") 
public class ApplicationController implements BaseController {
    private int aId;
    private int lId; 
    private MainController mainController;  
    
    @FXML private TextField resumeField;
    @FXML private TextField coverLetterField;

    @FXML public void backToListings() { mainController.loadListingsView(aId, null); }
    
    public void submit() {
        String resume = resumeField.getText();
        String coverLetter = coverLetterField.getText();
        try {
            int appId = Application.create(aId, lId, resume, coverLetter);
            mainController.showErrorAlert("Success", "Application created succcessfully");
            mainController.loadHomeView(aId);
            Listing listing = Listing.getById(lId);
            Notification.create(listing.getRecruiterId(), "New application to listing:\n"+listing.toString()+"\nBy:\n"+Auditionee.getById(aId).toString());
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "Error creating application:\n\t" + e.getMessage());
        }
    }

    public void setUserData(int uId, int lId) {  
        this.aId = uId;
        this.lId = lId; 
    } 

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

package com.tems.controllers;

import java.sql.SQLException;

import com.tems.models.TalentRecruiter;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TalentRecruiterHomeController extends MainController{
    
    TalentRecruiter recruiter; 

    @FXML
    private Label infoLabel;

    @FXML
    public void initialize() {
        infoLabel.setText(recruiter.toString());
    }
    
    public void setUserData(int id) {
        try {
            this.recruiter = TalentRecruiter.getById(id);
        } catch (SQLException e) {
            showErrorAlert("Error", "An error occurred while loading the user data. Please try again later.");
        } 
    }

    
}

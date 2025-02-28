package com.tems.controllers;

import com.tems.models.Auditionee;
import com.tems.models.Genre;
import com.tems.models.TalentRecruiter;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

@SuppressWarnings("unused") 
public class ListingController implements BaseController {
    private Auditionee auditionee;
    private TalentRecruiter recruiter;

    private MainController mainController;

    @FXML
    private ListView<Genre> genreListView;
 
    // Auditionee methods
    @FXML
    public void initialize() {
        // Populate the ComboBox with the Gender enum values

        //genreListView.().addAll(Gender.values());
        if (auditionee != null) {
            //genreListView.getItems().addAll(auditionee.getGenres());

        } else if(recruiter != null) {
            
        }
    }

    @FXML
    private void updateFilters() {
        
    }

    

    // Talent Recruiter methods


    // General
    public void setUserData(Auditionee user) { 
        this.auditionee = user; 
    }

    public void setUserData(TalentRecruiter user) {
        this.recruiter = user;
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

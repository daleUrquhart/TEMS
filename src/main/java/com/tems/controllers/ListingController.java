package com.tems.controllers;

import java.sql.SQLException;

import com.tems.models.Auditionee; 
import com.tems.models.Genre;
import com.tems.models.Listing;
import com.tems.models.TalentRecruiter;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;

@SuppressWarnings("unused") 
public class ListingController implements BaseController {
    private Auditionee auditionee;
    private TalentRecruiter recruiter;

    private MainController mainController;

    @FXML
    private CheckComboBox<Genre> genreComboBox;
 
    @FXML
    private VBox listingBox;

    // Auditionee methods 
    @FXML
    public void initialize() {}

    @FXML
    private void updateFilters() {
        
    }

    

    // Talent Recruiter methods


    // General
    public void setUserData(int id) { 
        try {
            this.auditionee = Auditionee.getById(id);
            // Load genres into genre filter
            ObservableList<Genre> genres = FXCollections.observableArrayList(Genre.getAll());
            genreComboBox.getItems().setAll(genres);

            // Load roles compatable with auditionee's preferred gender roles
            for(Listing listing : Listing.getByGenders(auditionee.getGenderRoles())) {
                listingBox.getChildren().add(new Label(listing.toString()));
                // Add listing data for auditionee here TODO implement getGenderRoles first
            } 
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "Error loading Listings View: \n\t" + e.getMessage());
        }
    } 

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

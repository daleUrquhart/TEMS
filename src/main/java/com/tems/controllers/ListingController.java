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
import com.jfoenix.controls.*;
import org.controlsfx.control.CheckComboBox;

import javafx.scene.layout.HBox;

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
            HBox lBox;
            Label info;
            JFXButton apply;

            for(Listing listing : Listing.getByGenders(auditionee.getGenderRoles())) {
                lBox = new HBox();
                info = new Label(listing.toString());
                apply = new JFXButton("Apply");
                apply.onMouseClickedProperty().set(eh -> mainController.loadApplicationView(auditionee.getUserId(), listing.getListingId()));
                lBox.getChildren().addAll(info, apply); 
                listingBox.getChildren().add(lBox);
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

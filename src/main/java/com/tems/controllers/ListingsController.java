package com.tems.controllers;

import java.sql.SQLException; 
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox; 

import com.jfoenix.controls.JFXButton;
import com.tems.models.Application;
import com.tems.models.Auditionee;
import com.tems.models.Genre;
import com.tems.models.Listing;
import com.tems.models.TalentRecruiter;
import com.tems.models.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller for AudListingsView and TRListingsView loading listings for view
 * @author Dale Urquhart
 */
@SuppressWarnings("unused") 
public class ListingsController implements BaseController {
    private Auditionee auditionee;
    private TalentRecruiter recruiter;
    private int id;

    private MainController mainController;
    private List<Genre> filters;

    @FXML
    private CheckComboBox<Genre> genreComboBox;
 
    @FXML private VBox listingBox; 

    @FXML private void handleHomeView() { mainController.loadHomeView(id); } 

    /**
     * Update filters and reload listings
     */
    @FXML private void updateFilters() { 
        ObservableList<Genre> checked = genreComboBox.getCheckModel().getCheckedItems();
        filters = checked.subList(0, checked.size()-1 < 0 ? 0 : checked.size()); 
        mainController.loadListingsView(id, filters);
    }  

    public void setUserData(int id, List<Genre> f) { 
        this.id = id;
        this.filters = f;
        if(this.filters == null) this.filters = new ArrayList<>();
        try {
            HBox lBox;
            Label info;
            if(User.getById(id).getRole().equals("auditionee")) {
                // Aud
                this.auditionee = Auditionee.getById(id);

                // Load genres into genre filter options
                ObservableList<Genre> genres = FXCollections.observableArrayList(Genre.getAll());
                genreComboBox.getItems().setAll(genres);
                // Load roles compatable with auditionee's preferred gender roles and Apply filters
                JFXButton apply;
                boolean offerSent = false, filteredOut = false;
                for(Listing listing : Listing.getByGenders(auditionee.getGenderRoles())) {
                    for(Application a : Application.getByListingId(listing.getListingId())) {
                        if(a.getStatus().equals("accepted")) offerSent = true; 
                    } if(offerSent) { 
                        offerSent = false;
                        continue;
                    } 
                    for(Genre g : filters) {
                        if(listing.getGenres().contains(g)) {
                            filteredOut = true;
                            break;
                        } 
                    } if(filteredOut) {
                        filteredOut = false;
                        continue;
                    }

                    lBox = new HBox();
                    info = new Label(listing.toString());
                    apply = new JFXButton("Apply");
                    apply.onMouseClickedProperty().set(eh -> mainController.loadApplicationView(auditionee.getUserId(), listing.getListingId()));
                    lBox.getChildren().addAll(info, apply); 
                    listingBox.getChildren().add(lBox);
                } 
            } else {
                //TR
                this.recruiter = TalentRecruiter.getById(id);
 
                JFXButton viewApps;
                JFXButton update;
                JFXButton delete;
                boolean offerSent=false;
                for(Listing listing : Listing.getByTRId(id)) {
                    for(Application a : Application.getByListingId(listing.getListingId())) {
                        if(a.getStatus().equals("accepted")) {
                            offerSent = true;
                        }
                    } if(offerSent) {
                        offerSent = false;
                        continue;
                    }
                    lBox = new HBox();
                    info = new Label(listing.toString());
                    viewApps = new JFXButton("View Applications");
                    viewApps.onMouseClickedProperty().set(eh -> mainController.loadApplicationsView(recruiter.getUserId(), listing.getListingId()));
                    update = new JFXButton("Update");
                    update.onMouseClickedProperty().set(eh -> mainController.loadEditListingView(listing.getListingId()));
                    delete = new JFXButton("Delete");
                    delete.onMouseClickedProperty().set(eh -> {
                        try{
                            Listing.delete(listing.getListingId());
                            mainController.loadListingsView(id, filters);
                        } catch(SQLException e) {
                            mainController.showErrorAlert("Errror", "Error deleting listing: \n\t"+e.getMessage());
                        }
                    });
                    lBox.getChildren().addAll(info, viewApps, update, delete); 
                    listingBox.getChildren().add(lBox);
                } 
            }
            if(listingBox.getChildren().isEmpty()) listingBox.getChildren().add(new Label("No Listings To Display"));
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "Error loading Listings View: \n\t" + e.getMessage());
        }
    } 

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

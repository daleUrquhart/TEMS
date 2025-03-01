package com.tems.controllers; 


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.CheckComboBox; 

import com.jfoenix.controls.JFXTextField;
import com.tems.models.CriteriaType;
import com.tems.models.Gender;
import com.tems.models.Genre;
import com.tems.models.Listing;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EditListingController implements BaseController{
    private MainController mainController;
    private int uId; 
    private Listing listing;

    @FXML TextField titleField;
    @FXML TextField descriptionField;
    @FXML CheckComboBox<Genre> genreComboBox;
    @FXML CheckComboBox<Gender> genderRoleComboBox;
    @FXML CheckComboBox<CriteriaType> criteriaComboBox; 
    @FXML private VBox weightBox;
    private final Map<CriteriaType, TextField> criteriaWeightMap = new HashMap<>();

    @Override public void setMainController(MainController mainController) { this.mainController = mainController; }
    @FXML public void handleHomeView() { mainController.loadHomeView(uId); }
    @FXML public void backToListings() { mainController.loadListingsView(uId); }

    @FXML public void submit() { 
        try {
            String title = titleField.getText();
            String description = descriptionField.getText();
            List<Genre> genres  = genreComboBox.getCheckModel().getCheckedItems();
            List<Gender> genderRoles  = genderRoleComboBox.getCheckModel().getCheckedItems();
            
            Map<CriteriaType, Integer> selectedCriteria = new HashMap<>();

            for (Map.Entry<CriteriaType, TextField> entry : criteriaWeightMap.entrySet()) {
                try {
                    int weight = Integer.parseInt(entry.getValue().getText());
                    selectedCriteria.put(entry.getKey(), weight);
                } catch (NumberFormatException e) {
                    mainController.showErrorAlert("Error", "Please enter a valid integer for all criteria.");
                    return;
                }
            }
            
            if(title.strip().isEmpty() || description.strip().isEmpty()) throw new SQLException("Cannot have blank title or description");
            listing.setTitle(title);
            listing.setDescription(description);
            listing.setCriteria(selectedCriteria);
            listing.setGenders(genderRoles);
            listing.setGenres(genres);
            listing.update();
            mainController.showErrorAlert("Success", "Listing updated succcessfully");
            mainController.loadListingsView(uId);
        }
        catch(SQLException e) {
            mainController.showErrorAlert("Error", "Error editing listing: "+e.getMessage());
            clearFields();
        }
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        genreComboBox.getCheckModel().clearChecks();
        criteriaComboBox.getCheckModel().clearChecks();
        genderRoleComboBox.getCheckModel().clearChecks();
    }

    /**
     * Updates the listing data
     * @param lId Listing's ID
     */
    public void setUserData(int lId) { 
        try {
            this.listing = Listing.getById(lId);
            this.uId = listing.getRecruiterId();
            // Load combo boxes with options and preselect current data
            ObservableList<Genre> genres = FXCollections.observableArrayList(Genre.getAll());
            ObservableList<CriteriaType> criteria = FXCollections.observableArrayList(CriteriaType.getAll());
            ObservableList<Gender> genders = FXCollections.observableArrayList(Gender.getAll());
            
            titleField.setText(listing.getTitle());
            descriptionField.setText(listing.getDescription());

            genreComboBox.getItems().setAll(genres); 
            criteriaComboBox.getItems().setAll(criteria); 
            genderRoleComboBox.getItems().setAll(genders);

            for(Gender g : listing.getGenderRoles()) {genderRoleComboBox.getCheckModel().check(g); }
            for(Genre g : listing.getGenres()) {genreComboBox.getCheckModel().check(g); }
            for(Map.Entry<CriteriaType, Integer> entry : listing.getCriteria().entrySet()) {criteriaComboBox.getCheckModel().check(entry.getKey()); }
        }
        catch(SQLException e) {
            mainController.showErrorAlert("Error", "Error setting up edit listing view.\n\t"+e.getMessage());
        }
    }

    @FXML public void initialize() { criteriaComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<CriteriaType>) change -> updateWeightFields()); }

    private void updateWeightFields() {
        weightBox.getChildren().clear();  // Clear previous fields 
        criteriaWeightMap.clear();

        for (CriteriaType criteria : criteriaComboBox.getCheckModel().getCheckedItems()) {
            HBox row = new HBox(10);
            Label label = new Label(criteria.getName() + " Weight:");
            JFXTextField weightField = new JFXTextField();
            weightField.setPromptText("Enter weight");

            row.getChildren().addAll(label, weightField);
            weightBox.getChildren().add(row);

            criteriaWeightMap.put(criteria, weightField);
        }
    }
}




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

public class ListingController implements BaseController{
    private MainController mainController;
    private int id;
    @FXML TextField titleField;
    @FXML TextField descriptionField;
    @FXML CheckComboBox<Genre> genreComboBox;
    @FXML CheckComboBox<Gender> genderRoleComboBox;
    @FXML CheckComboBox<CriteriaType> criteriaComboBox; 
    @FXML private VBox weightBox;
    private final Map<CriteriaType, TextField> criteriaWeightMap = new HashMap<>();

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML public void create() { 
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
            Listing.create(id, title, description, genderRoles, genres, selectedCriteria);
            mainController.showErrorAlert("Success", "Listing created succcessfully");
            mainController.loadHomeView(id);
        }
        catch(SQLException e) {
            mainController.showErrorAlert("Error", "Error creating listing: "+e.getMessage());
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

    public void setUserData(int id) {
        this.id = id;
        try {
            ObservableList<Genre> genres = FXCollections.observableArrayList(Genre.getAll());
            genreComboBox.getItems().setAll(genres);
            ObservableList<CriteriaType> criteria = FXCollections.observableArrayList(CriteriaType.getAll());
            criteriaComboBox.getItems().setAll(criteria);
            ObservableList<Gender> genders = FXCollections.observableArrayList(Gender.getAll());
            genderRoleComboBox.getItems().setAll(genders);
        }
        catch(SQLException e) {
            mainController.showErrorAlert("Error", "Error setting up create listing view.");
        }
    }

    @FXML
    public void initialize() {
        criteriaComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<CriteriaType>) change -> updateWeightFields());
    }

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

    @FXML
    public void handleHomeView() {
        mainController.loadHomeView(id);
    }
}

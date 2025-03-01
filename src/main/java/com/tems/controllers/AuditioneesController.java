package com.tems.controllers; 

import java.sql.SQLException;

import org.controlsfx.control.CheckComboBox; 

import com.jfoenix.controls.JFXButton;
import com.tems.models.Auditionee;
import com.tems.models.Genre;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") 
public class AuditioneesController implements BaseController { 
    private int id; 
    private MainController mainController; 
    @FXML private CheckComboBox<Genre> genreComboBox; 
    @FXML private VBox auditioneeBox;   
    @FXML private void handleHomeView() { mainController.loadHomeView(id); } 

    // General
    public void setUserData(int id) { 
        this.id = id;
        try {
            HBox aBox;
            Label info;
            JFXButton update;
            JFXButton delete;

            for(Auditionee auditionee : Auditionee.getAll()) {
                aBox = new HBox();
                info = new Label(auditionee.toString());
                update = new JFXButton("Update");
                update.onMouseClickedProperty().set(eh -> mainController.loadEditProfileView(auditionee.getUserId()));
                delete = new JFXButton("Delete");
                delete.onMouseClickedProperty().set(eh -> {
                    try{
                        Auditionee.delete(auditionee.getUserId());
                        mainController.loadAuditioneesView(id);
                    } catch(SQLException e) {
                        mainController.showErrorAlert("Errror", "Error deleting auditionee: \n\t"+e.getMessage());
                    }
                });
                aBox.getChildren().addAll(info, update, delete); 
                auditioneeBox.getChildren().add(aBox);
            } 
            if(auditioneeBox.getChildren().isEmpty()) auditioneeBox.getChildren().add(new Label("No Auditionees To Display"));
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "Error loading auditionee View: \n\t" + e.getMessage());
        }
    } 

    @Override public void setMainController(MainController mainController) { this.mainController = mainController; }
} 
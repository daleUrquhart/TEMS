package com.tems.controllers; 

import java.sql.SQLException;

import org.controlsfx.control.CheckComboBox; 

import com.jfoenix.controls.JFXButton;
import com.tems.models.Auditionee;
import com.tems.models.Genre;
import com.tems.models.TalentRecruiter;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") 
public class ProfilesController implements BaseController { 
    private int id; 
    private char type;
    private MainController mainController; 
    @FXML private CheckComboBox<Genre> genreComboBox; 
    @FXML private VBox profileBox;   
    @FXML private void handleHomeView() { mainController.loadHomeView(id); } 

    // General
    public void setUserData(int id, char type) { 
        this.id = id;
        this.type = type;
        try {
            if(type=='A') listAuditionees(); 
            else listTRs();
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "Error loading auditionee View: \n\t" + e.getMessage());
        }
    }  

    private void listTRs() throws SQLException {
        HBox aBox;
        Label info;
        JFXButton update;
        JFXButton delete;
        for(TalentRecruiter tr : TalentRecruiter.getAll()) {
            aBox = new HBox();
            info = new Label(tr.toString());
            update = new JFXButton("Update");
            update.onMouseClickedProperty().set(eh -> mainController.loadEditProfileView(id, tr.getUserId()));
            delete = new JFXButton("Delete");
            delete.onMouseClickedProperty().set(eh -> {
                try{
                    TalentRecruiter.delete(tr.getUserId());
                    mainController.loadProfilesView(id, type);
                } catch(SQLException e) {
                    mainController.showErrorAlert("Errror", "Error deleting profile: \n\t"+e.getMessage());
                }
            });
            aBox.getChildren().addAll(info, update, delete); 
            profileBox.getChildren().add(aBox);
        } 
        if(profileBox.getChildren().isEmpty()) profileBox.getChildren().add(new Label("No Profiles To Display"));
    }

    private void listAuditionees() throws SQLException {
        HBox aBox;
        Label info;
        JFXButton update;
        JFXButton delete;
        for(Auditionee auditionee : Auditionee.getAll()) {
            aBox = new HBox();
            info = new Label(auditionee.toString());
            update = new JFXButton("Update");
            update.onMouseClickedProperty().set(eh -> mainController.loadEditProfileView(id, auditionee.getUserId()));
            delete = new JFXButton("Delete");
            delete.onMouseClickedProperty().set(eh -> {
                try{
                    Auditionee.delete(auditionee.getUserId());
                    mainController.loadProfilesView(id, type);
                } catch(SQLException e) {
                    mainController.showErrorAlert("Errror", "Error deleting profile: \n\t"+e.getMessage());
                }
            });
            aBox.getChildren().addAll(info, update, delete); 
            profileBox.getChildren().add(aBox);
        } 
        if(profileBox.getChildren().isEmpty()) profileBox.getChildren().add(new Label("No Profiles To Display"));
    }
    @Override public void setMainController(MainController mainController) { this.mainController = mainController; }
} 
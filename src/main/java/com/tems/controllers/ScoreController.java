package com.tems.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tems.models.Application;
import com.tems.models.Criteria;
import com.tems.models.CriteriaType;
import com.tems.models.Listing;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ScoreController implements BaseController {
    private int uId; 
    private int lId;
    private Application app;

    private MainController mainController; 
    private List<TextField> entries;
    private List<Criteria> criteria;

    @FXML VBox scoreBox;
    @FXML public void backToApp() { mainController.loadApplicationsView(uId, lId); } 

    public void submit() {  
        int[] scores = new int[criteria.size()];

        for(int i = 0; i < entries.size(); i++) {
            try {
                scores[i] = Integer.parseInt(entries.get(i).getText());
            } catch (NumberFormatException e) {
                mainController.showErrorAlert("Error", "Scores must be valid integers.");
            } 
        try {
            app.setFinalScore(criteria, scores);   
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "Error assigning scores: \n\t"+e.getMessage());
        }
        }  
        mainController.showErrorAlert("Success", "Successfull scored application.");
        backToApp();
    }

    public void setUserData(int appId) {  
        this.app = Application.getById(appId);
        this.lId = app.getListingId();
        this.entries = new ArrayList<>();
        this.criteria = new ArrayList<>();

        HBox cBox;
        try {
            this.uId = Listing.getById(lId).getRecruiterId();
            for(Map.Entry<CriteriaType, Integer> entry : Criteria.getByListingId(lId).entrySet()) {
                cBox = new HBox();
                Label l = new Label(entry.getKey().getName());
                TextField tf = new TextField();
                tf.setPromptText("Enter score");
                cBox.getChildren().addAll(l, tf);
                entries.add(tf);
                scoreBox.getChildren().add(cBox);
            }
        } catch(SQLException e) {
            mainController.showErrorAlert("Error", "Error getting listing criteria: \n\t"+e.getMessage());
        }
    } 

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

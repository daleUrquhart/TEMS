package com.tems.controllers;

import java.sql.SQLException;

import com.tems.models.Auditionee; 
import com.tems.models.Application;  
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox; 

import javafx.scene.layout.HBox;

@SuppressWarnings("unused") 
public class ApplicationsController implements BaseController {
    private int id;

    private MainController mainController; 
 
    @FXML
    private VBox applicationsBox; 

    @FXML 
    public void handleHomeView() {
        mainController.loadHomeView(id);
    }
    

    // Talent Recruiter methods


    // General
    public void setUserData(int id) { 
        this.id = id;
        HBox lBox;
        Label info; 

        for(Application application : Application.getByAudId(id)) {
            lBox = new HBox();
            info = new Label(application.toString()); 
            lBox.getChildren().addAll(info); 
            applicationsBox.getChildren().add(lBox);
        }  
    } 

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

package com.tems.controllers;

import java.sql.SQLException;

import com.jfoenix.controls.JFXButton;
import com.tems.models.Application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") 
public class ApplicationsController implements BaseController {
    private int uId;

    private MainController mainController; 
 
    @FXML private VBox applicationsBox; 
 
    @FXML public void backToListings() { mainController.loadListingsView(uId, null); }
    @FXML public void handleHomeView() { mainController.loadHomeView(uId); }
     
    /**
     * Load every application for auditionee
     * @param id
     */
    public void setUserData(int audId) { 
        this.uId = audId;
        HBox lBox;
        Label info; 

        for(Application application : Application.getByAudId(audId)) {
            lBox = new HBox();
            info = new Label(application.toString()); 
            lBox.getChildren().addAll(info); 
            applicationsBox.getChildren().add(lBox);
        }  
    } 

    /**
     * Load every application for listing 
     */
    public void setUserData(int TRId, int lId) {
        this.uId = TRId;
        HBox lBox;
        Label info; 
        JFXButton score;
        JFXButton accept;
        JFXButton decline; 
        int avgScore = 0, scoreCount = 0;
        for(Application application : Application.getByListingId(lId)) {
            
            lBox = new HBox();
            info = new Label(application.toString()); 
            score = new JFXButton("Evaluate"); 
            score.onMouseClickedProperty().set(eh -> {mainController.loadScoreView(application.getApplicationId()); }); 
            accept = new JFXButton("Accept");
            accept.onMouseClickedProperty().set(eh -> {
                try{
                    Application.accept(application.getAuditioneeId(), lId);
                    mainController.loadApplicationsView(uId, lId);
                }
                catch(SQLException e){
                    mainController.showErrorAlert("Error", "Error Accepting applicaiotn: \n\t"+e.getMessage());
                }
            });
            decline = new JFXButton("Decline");
            decline.onMouseClickedProperty().set(eh -> {
                try{
                    Application.decline(application.getAuditioneeId(), lId);
                    mainController.loadApplicationsView(uId, lId);
                }
                catch(SQLException e){
                    mainController.showErrorAlert("Error", "Error Declineing applicaiotn: \n\t"+e.getMessage());
                }
            });
            lBox.getChildren().addAll(info, score, accept, decline); 
            applicationsBox.getChildren().add(lBox);
            if(application.getFinalScore() != 0) { avgScore += application.getFinalScore(); scoreCount++; }
        }  
        if(applicationsBox.getChildren().isEmpty()) {applicationsBox.getChildren().add(new Label("No applicaitons to display")); }
        else {
            Label avgLabel = new Label("Average score: "+(scoreCount==0 ? 0 : avgScore / scoreCount));
            applicationsBox.getChildren().add(avgLabel);
        }
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

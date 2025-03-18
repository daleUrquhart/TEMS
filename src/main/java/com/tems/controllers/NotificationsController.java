package com.tems.controllers;

import java.sql.SQLException;

import com.jfoenix.controls.JFXButton;
import com.tems.models.Notification;
import com.tems.models.TalentRecruiter;
import com.tems.util.Env;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NotificationsController implements BaseController {
    private MainController mainController;
    private int id;

    @FXML VBox notificationsBox;


    public void setUserData(int id) {
        this.id = id;
        HBox nBox;
        try {
            for(Notification notification : Notification.getByUserId(id)) {
                nBox = new HBox();
                nBox.getChildren().add(new Label(notification.getMessage()));
                if(id == Env.ADMIN_ID) {
                    JFXButton review = new JFXButton("Review Request");
                    JFXButton decline = new JFXButton("Decline Request");
                    JFXButton accept = new JFXButton("Accept Request");
                    review.onMouseClickedProperty().set(eh -> {
                        mainController.loadTRCreateView();
                    });
                    decline.onMouseClickedProperty().set(eh -> {
                        try {
                            notification.delete();
                            mainController.loadNotificationsView(id);
                        } catch(SQLException e) {
                            mainController.showErrorAlert("Error", "Error declining request.");
                        }
                    });
                    accept.onMouseClickedProperty().set(eh -> {
                        String[] lines = notification.getMessage().split("\n");

                        // Extract fields using prefixes
                        String name = lines[1].replace("Name: ", "").trim();
                        String email = lines[2].replace("Email: ", "").trim();
                        String company = lines[3].replace("Company: ", "").trim();
                        String passwordHash = lines[4].replace("Password hash: ", "").trim(); 

                        // Create the TalentRecruiter and delete notificaiton after
                        try {
                            TalentRecruiter.create(name, email, passwordHash, company);  
                            notification.delete(); 
                        } catch (SQLException e) {
                            mainController.showErrorAlert("Error", "Error accepting request.");
                        }
                        mainController.loadNotificationsView(id);
                    });
                    nBox.getChildren().addAll(review, decline, accept);
                }
                notificationsBox.getChildren().add(nBox);
            }
            if(Notification.getByUserId(id).isEmpty()) notificationsBox.getChildren().add(new Label("No Notifications To Display"));
        } catch(SQLException e) {
            mainController.showErrorAlert("Error", "Error laoding notifications");
        }
    }

    @FXML 
    public void handleHomeView() {
        mainController.loadHomeView(id);
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    } 
}

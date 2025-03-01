package com.tems.controllers;

import java.sql.SQLException;

import com.tems.models.Notification;

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

package com.tems.controllers; 

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType; 
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") // FXML methods are highlighted as unused even if that is not the case
public class MainController { 

    @FXML
    private BorderPane mainPane;  

    @FXML
    private void loadSignUpView() {
        loadView("/views/SignUpView.fxml");
    }

    @FXML
    private void loadSignInView() {
        loadView("/views/SignInView.fxml");
    }

    // Loads centre view of the BorderPane in MainView.fxml
    private void loadView(String fxmlPath) { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            VBox view = loader.load();

            // Set the loaded view into the center of the BorderPane
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while loading the view. Please try again later.");
        }
    }

    @FXML
    void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); 
        alert.showAndWait();
    } 
} 

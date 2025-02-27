package com.tems.controllers; 

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") 
public class MainController { 

    @FXML private BorderPane mainPane;  // Universal BorderPane 

    // Generic method to load views dynamically
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Object view = loader.load(); 

            // Get the controller from the loaded FXML
            Object controller = loader.getController();
            
            // If the loaded controller extends BaseController, set its main controller
            if (controller instanceof BaseController baseController) baseController.setMainController(this);

            // Set the mainPane's new content based on the loaded view type
            if (view instanceof VBox) {
                // Subview case 
                mainPane.setCenter((VBox) view);
            } else if (view instanceof BorderPane) {
                // Main view case (replace full scene root)
                mainPane.getScene().setRoot((BorderPane) view); 
                if (controller instanceof MainController) {
                    ((MainController) controller).setMainPane((BorderPane) view);
                }
            } else {
                // Unsupported layout case
                throw new IllegalStateException("Unsupported layout type: " + view.getClass().getSimpleName());
            }

            // Update MainController reference to ensure it's always managing the current view
            if (controller instanceof MainController mainController) mainController.setMainPane(mainPane); // Pass mainPane reference if needed

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while loading the view.\n" + e.getMessage());
        }
    }

    // Setter method to update mainPane reference
    public void setMainPane(BorderPane mainPane) {
        this.mainPane = mainPane;
    }

    // Load main view
    public void loadMainView() {
        
        loadView("/views/MainView.fxml");
    }

    @FXML
    private void loadSignUpView() {
        loadView("/views/SignUpView.fxml");
    }

    @FXML
    private void loadSignInView() {
        loadView("/views/SignInView.fxml");
    }

    // Display error alerts
    public void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); 
        alert.setResizable(true); 
        alert.showAndWait();
    } 
}  

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
public class MainController implements BaseController { 

    @FXML private BorderPane mainPane;  
    @FXML private MainController mainController;

    public BorderPane getMainPane() {
        return mainPane;
    }
 
    private Object loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Object view = loader.load();  
            BaseController controller = loader.getController(); 
            controller.setMainController(this); 
            
            
            
            if (view instanceof VBox v) {
                mainPane.setCenter(v);  
            } 
            else if (view instanceof BorderPane v) {   
                mainPane.getScene().setRoot(v); 
                //((MainController)controller).setMainPane(v);
            } 
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while loading the view.\n" + e.getMessage());
            return mainController; 
        }
    } 

    // Load main view
    @FXML
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

    @FXML
    void loadAudHomeView(int id) { 
        String fxmlPath = "/views/AuditioneeHomeView.fxml"; 
        AuditioneeHomeController controller = (AuditioneeHomeController) loadView(fxmlPath);
        controller.setUserData(id);
    }

    @FXML
    void loadTRHomeView(int id) { 
        String fxmlPath = "/views/TalentRecruiterHomeView.fxml";
        TalentRecruiterHomeController controller = (TalentRecruiterHomeController) loadView(fxmlPath);
        controller.setUserData(id); 
    } 

    @FXML
    void loadAudListingView(int id) {
        String fxmlPath = "/views/AuditioneeListingView.fxml";
        ListingController controller = (ListingController) loadView(fxmlPath);
        controller.setUserData(id); 
    }
    @Override
    public void setMainController(MainController mainController) {
        this.mainController = this;
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

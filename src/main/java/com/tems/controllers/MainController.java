package com.tems.controllers; 
 
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

import com.sun.jna.platform.linux.ErrNo;
import com.tems.models.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; 
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") 
public class MainController implements BaseController { 

    @FXML private BorderPane mainPane;  
    @FXML private MainController mainController;
    @FXML private ImageView imageView;

    

    @FXML 
    public void logoClicked() {
        loadMainView();
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
 
    public void initialize() {
        // Load image correctly from the resources folder
        String path = "/images/logo.jpg";
        URL imageUrl = getClass().getResource(path); 
        if (imageUrl != null) {
            Image image = new Image(imageUrl.toExternalForm());
            imageView.setImage(image);
        } else {
            System.err.println("Error: Logo image not found at "+path);
        }
    }

    private Object loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Object view = loader.load();  
            BaseController controller = loader.getController(); 
            controller.setMainController(this); 
            
            
            
            switch (view) {
                case VBox v -> mainPane.setCenter(v);
                case BorderPane v -> mainPane.getScene().setRoot(v);  
                default -> {}
            }
            return controller;
        } catch (IOException e) { 
            showErrorAlert("Error", "An error occurred while loading the view.\n" + e.getMessage());
            return mainController; 
        }
    } 

    public BorderPane getMainPane() {
        return mainPane;
    }
 
    void loadAudListingView(int id) {
        String fxmlPath = "/views/AuditioneeListingView.fxml";
        ListingController controller = (ListingController) loadView(fxmlPath);
        controller.setUserData(id); 
    }
 
    void loadApplicationView(int uId, int lId) {
        String fxmlPath = "/views/ApplicationView.fxml";
        ApplicationController controller = (ApplicationController) loadView(fxmlPath);
        controller.setUserData(uId, lId);
    }
 
    void loadApplicationsView(int uId) {
        String fxmlPath = "/views/ApplicationsView.fxml";
        ApplicationsController controller = (ApplicationsController) loadView(fxmlPath);
        controller.setUserData(uId);
    }

    void loadNotificationsView(int id) {
        String fxmlPath = "/views/NotificationsView.fxml";
        NotificationsController controller = (NotificationsController) loadView(fxmlPath);
        controller.setUserData(id);
    }

    void loadEditProfileView(int id) {
        try{
            String fxmlPath;
            if(User.getById(id).getRole().equals("auditionee")) fxmlPath = "/views/EditAudProfileView.fxml";
            else fxmlPath = "/views/EditTRProfileView.fxml";
            EditProfileController controller = (EditProfileController) loadView(fxmlPath);
            controller.setUserData(id);
        }
        catch(SQLException e) {
            showErrorAlert("Error", "Error going to edit profile view: \n\t"+e.getMessage());
        }
    } 

    void loadHomeView(int id) {
        try{
            String fxmlPath;
            if(User.getById(id).getRole().equals("auditionee")) {
                fxmlPath = "/views/AuditioneeHomeView.fxml";
                AuditioneeHomeController controller = (AuditioneeHomeController) loadView(fxmlPath);
                controller.setUserData(id);
            }
            else {
                fxmlPath = "/views/TalentRecruiterHomeView.fxml";
                TalentRecruiterHomeController controller = (TalentRecruiterHomeController) loadView(fxmlPath);
                controller.setUserData(id);
            }
            //EditProfileController controller = (EditProfileController) loadView(fxmlPath); TODO implement it like other pages??
            //controller.setUserData(id);
        }
        catch(SQLException e) {
            showErrorAlert("Error", "Error going to edit profile view: \n\t"+e.getMessage());
        }
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

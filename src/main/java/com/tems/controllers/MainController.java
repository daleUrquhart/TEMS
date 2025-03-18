package com.tems.controllers; 
 
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import com.tems.models.User;
import com.tems.util.Env;

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

    // Load request TR account view
    @FXML
    public void loadRequestView() { 
        loadView("/views/RequestView.fxml");
    } 

    @FXML
    private void loadSignUpView() {
        SignUpController controller = (SignUpController) loadView("/views/SignUpView.fxml");
        controller.initializeAudView();
    }

    @FXML
    void loadTRCreateView() {
        loadView("/views/TRCreateView.fxml");
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
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while loading the view.\n" + e.getMessage());
            return mainController; 
        }
    } 

    public BorderPane getMainPane() {
        return mainPane;
    }
 
    void loadApplicationView(int uId, int lId) {
        String fxmlPath = "/views/ApplicationView.fxml";
        ApplicationController controller = (ApplicationController) loadView(fxmlPath);
        controller.setUserData(uId, lId);
    }
 
    void loadApplicationsView(int uId) {
        String fxmlPath = "/views/AudAppsView.fxml";
        ApplicationsController controller = (ApplicationsController) loadView(fxmlPath);
        controller.setUserData(uId);
    }

    void loadApplicationsView(int uId, int lId) {
        String fxmlPath = "/views/TRAppsView.fxml";
        ApplicationsController controller = (ApplicationsController) loadView(fxmlPath);
        controller.setUserData(uId, lId);
    }

    void loadScoreView(int id) {
        String fxmlPath = "/views/ScoreView.fxml";
        ScoreController controller = (ScoreController) loadView(fxmlPath);
        controller.setUserData(id);
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

    void loadEditProfileView(int editorId, int editeeId) {
        String fxmlPath;
        if(editorId == Env.ADMIN_ID) fxmlPath = "/views/EditTRProfileView.fxml";
        else fxmlPath = "/views/EditAudProfileView.fxml";
        EditProfileController controller = (EditProfileController) loadView(fxmlPath);
        controller.setUserData(editorId, editeeId);
    }

    void loadEditListingView(int lId) {
        String fxmlPath = "/views/EditListingView.fxml";
        EditListingController controller = (EditListingController) loadView(fxmlPath);
        controller.setUserData(lId); 
    } 

    void loadHomeView(int id) {
        try{
            String fxmlPath;
            if(User.getById(id).getRole().equals("admin")) fxmlPath = "/views/AdminHomeView.fxml";
            else if(User.getById(id).getRole().equals("auditionee")) fxmlPath = "/views/AudHomeView.fxml"; 
            else fxmlPath = "/views/TRHomeView.fxml"; 
            HomeController controller = (HomeController) loadView(fxmlPath);
            controller.setUserData(id);
        }
        catch(SQLException e) {
            showErrorAlert("Error", "Error going to edit profile view: \n\t"+e.getMessage());
        }
    }

    void loadListingView(int id) {
        String fxmlPath = "/views/ListingView.fxml";
        ListingController controller = (ListingController) loadView(fxmlPath);
        controller.setUserData(id);
    }

    void loadListingsView(int id) {
        try {
            String fxmlPath;
            if(User.getById(id).getRole().equals("auditionee")) fxmlPath = "/views/AudListingsView.fxml";
            else fxmlPath = "/views/TRListingsView.fxml";
            ListingsController controller = (ListingsController) loadView(fxmlPath);
            controller.setUserData(id); 
        } catch(SQLException e) {
            showErrorAlert("Error", "Error loading listings view");
        } 
    } 

    void loadProfilesView(int id, char type) {
        String fxmlPath = "/views/ProfilesView.fxml"; 
        ProfilesController controller = (ProfilesController) loadView(fxmlPath); 
        controller.setUserData(id, type);
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

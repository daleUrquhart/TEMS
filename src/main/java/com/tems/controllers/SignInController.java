package com.tems.controllers;

import java.io.IOException;
import java.sql.SQLException;

import com.tems.models.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Sign in controller
 * @author Dale Urquhart
 */
@SuppressWarnings("unused") 
public class SignInController implements BaseController {

    private MainController mainController;

    @FXML 
    private VBox mainBox;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleSignIn() {
        String email = emailField.getText();
        String password = passwordField.getText();
        try {
            User u = User.signIn(email, password);
            switch (u.getRole()) {
                case "auditionee" -> loadAudHomeView("/views/AuditioneeHomeView.fxml", u.getUserId());
                case "recruiter" -> loadTRHomeView("/views/TalentRecruiterHomeView.fxml", u.getUserId());
                default -> { // Load admin home
                }
            } 
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "An error occurred while signing in. \n"+e.getMessage());
            emailField.clear();
            passwordField.clear();
        }
    }  

    // Not finished, ignore for now
    void loadTRHomeView(String fxmlPath, int id) { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            VBox view = loader.load();

            // Set the loaded view into the center of the BorderPane
            TalentRecruiterHomeController controller = loader.getController();
            controller.setUserData(id);
            //main.setCenter(view); TODO

        } catch (IOException e) { 
            mainController.showErrorAlert("Error", "An error occurred while loading the view.\n\t"+e.getMessage());
        }
    }

    void loadAudHomeView(String fxmlPath, int id) { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            VBox view = loader.load();
            AuditioneeHomeController controller = loader.getController(); 
            controller.setMainController(mainController);

            // Set the loaded view into the center of the BorderPane
            BorderPane mainPane = (BorderPane) mainBox.getScene().getRoot(); 
            controller.setUserData(id);
            mainPane.setCenter(view);

        } catch (IOException e) { 
            e.printStackTrace();
            mainController.showErrorAlert("Error", "An error occurred while loading the view.\n\t"+e.getMessage());
        }
    }

    @FXML
    private void handleHomeView() {
        mainController.loadMainView();
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

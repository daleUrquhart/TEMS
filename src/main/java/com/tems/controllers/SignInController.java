package com.tems.controllers;
 
import java.sql.SQLException;

import com.tems.models.User;

import javafx.fxml.FXML; 
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField; 
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
            mainController.loadHomeView(u.getUserId());
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "An error occurred while signing in. \n"+e.getMessage());
            emailField.clear();
            passwordField.clear();
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

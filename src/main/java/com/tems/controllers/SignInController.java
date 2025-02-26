package com.tems.controllers;

import java.sql.SQLException;

import com.tems.models.User;
import com.tems.util.PasswordManager;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@SuppressWarnings("unused") 
public class SignInController extends MainController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleSignIn() {
        String email = emailField.getText();
        String password = passwordField.getText();
        try {
            User u = User.signIn(email, PasswordManager.hashPassword(password));
            if(u.getRole().equals("auditionee")) {
                // Redirect to auditionee role home page
            } else if(u.getRole().equals("recruiter")) {
                // Redirect to auditioner role home page
            } else {
                // Redirect to admin role home page
            }
        } catch (SQLException e) {
            showErrorAlert("Error", "An error occurred while signing in. Please try again later.");
            emailField.clear();
            passwordField.clear();
        }
    }

}

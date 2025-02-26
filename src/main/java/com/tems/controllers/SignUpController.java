package com.tems.controllers;

import com.tems.models.Auditionee;
import com.tems.models.Gender;
import com.tems.util.PasswordManager;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@SuppressWarnings("unused") 
public class SignUpController extends MainController{  

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<Gender> genderComboBox;  

    @FXML
    private TextField yoeField;  

    @FXML
    public void initialize() {
        // Populate the ComboBox with the Gender enum values
        genderComboBox.getItems().addAll(Gender.values());
    }

    @FXML
    private void handleSignUp() {
        // Get values from the input fields
        String username = nameField.getText(); 
        String password = passwordField.getText(); 
        String email = emailField.getText(); 
        Gender gender = genderComboBox.getValue();
        int yoe;
        try {
            yoe = Integer.parseInt(yoeField.getText());
        } catch(NumberFormatException e) {
            //alert the user that the years of experience must be a number
            showErrorAlert("Invalid Input", "Years of experience must be a valid number.");
            yoeField.clear();
            return;
        }

        // Print inputs
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println(gender.getDisplayName());
        System.out.println(yoe);


        // Create a new Auditionee with the gathered information and redirect to auditionee role home page
        Auditionee.create(username, email, PasswordManager.hashPassword(password), gender, yoe);
    }

}

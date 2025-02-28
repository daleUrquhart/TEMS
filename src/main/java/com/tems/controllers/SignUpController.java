package com.tems.controllers;
 
import java.sql.SQLException;

import com.tems.models.Auditionee;
import com.tems.models.Gender;
import com.tems.util.PasswordManager;

import javafx.fxml.FXML; 
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField; 
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") 
public class SignUpController implements BaseController{  

    private MainController mainController;

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
    private VBox mainBox;

    @FXML
    public void initialize() {
        // Populate the ComboBox with the Gender enum values
        genderComboBox.getItems().addAll(Gender.values());
    }

    @FXML
    private void handleHomeView() {
        mainController.loadMainView();
    }

    @FXML
    private void handleSignUp() {
        // Get values from the input fields
        String username = nameField.getText(); 
        String password = passwordField.getText();  
        Gender gender = genderComboBox.getValue();
        int yoe;
        String email = emailField.getText(); 

        // Validation
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()){
            mainController.showErrorAlert("Invalid Input", "Cannot have empty fields.");
            clearFields();

            return;
        }
        if (email.contains("@" ) == false || email.contains(".") == false){
            mainController.showErrorAlert("Invalid Input", "Must be a valid email address.");
            emailField.clear();
            return;
            
        }
        try {
            yoe = Integer.parseInt(yoeField.getText());
        } catch(NumberFormatException e) {
            //alert the user that the years of experience must be a number
            mainController.showErrorAlert("Invalid Input", "Years of experience must be a valid number.");
            yoeField.clear();
            return;
        } 

        // Create a new Auditionee with the gathered information and redirect to auditionee role home page
        try {
            int id = Auditionee.create(username, email, PasswordManager.hashPassword(password), gender, yoe);
            mainController.loadAudHomeView(id);
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "An error occurred while signing up: \n\t"+e.getMessage());
            clearFields();
        }
    } 

    private void clearFields() {
        emailField.clear();
        nameField.clear();
        passwordField.clear();
        yoeField.clear();
        genderComboBox.getSelectionModel().clearSelection();
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

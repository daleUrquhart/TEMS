package com.tems.controllers;
 
import java.sql.SQLException;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import com.tems.models.Auditionee;
import com.tems.models.Gender;
import com.tems.util.PasswordManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML; 
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField; 
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") 
public class SignUpController implements BaseController{  

    private MainController mainController;

    @FXML private TextField nameField;

    @FXML private TextField emailField;
    
    @FXML private PasswordField passwordField;

    @FXML private ComboBox<Gender> genderComboBox; 
    
    @FXML CheckComboBox<Gender> genderRoleComboBox;

    @FXML private TextField yoeField;  

    @FXML private VBox mainBox;

    @FXML public void initialize() {  
        try {
            genderComboBox.getItems().addAll(Gender.values());
            ObservableList<Gender> genders = FXCollections.observableArrayList(Gender.getAll());
            genderRoleComboBox.getItems().setAll(genders);
        } catch(SQLException e) { mainController.showErrorAlert("Error", "Error loading sign up data"); }
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
        List<Gender> genderRoles  = genderRoleComboBox.getCheckModel().getCheckedItems();
        int yoe;
        String email = emailField.getText();  
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
            mainController.showErrorAlert("Invalid Input", "Years of experience must be a valid number.");
            yoeField.clear();
            return;
        }  

        try {
            int id = Auditionee.create(username, email, PasswordManager.hashPassword(password), gender, yoe, genderRoles);
            mainController.loadHomeView(id);
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

package com.tems.controllers;
 
import java.sql.SQLException;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import com.tems.models.Auditionee;
import com.tems.models.Gender;
import com.tems.models.Notification;
import com.tems.models.TalentRecruiter;
import com.tems.util.Env;
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
    
    @FXML private TextField companyField;
    
    @FXML private PasswordField passwordField;

    @FXML private TextField infoField;

    @FXML private ComboBox<Gender> genderComboBox; 
    
    @FXML private CheckComboBox<Gender> genderRoleComboBox;

    @FXML private TextField yoeField;  

    @FXML private VBox mainBox;

    public void initializeAudView() {  
        try {
            genderComboBox.getItems().addAll(Gender.values());
            ObservableList<Gender> genders = FXCollections.observableArrayList(Gender.getAll());
            genderRoleComboBox.getItems().setAll(genders);
        } catch(SQLException e) { mainController.showErrorAlert("Error", "Error loading sign up data"); }
    }

    @FXML private void handleHomeView() { mainController.loadMainView(); }

    /**
     * Get TR Account request data and send a notification to the admin account
     */
    @FXML private void submitRequest() {
        String username = nameField.getText(); 
        String password = passwordField.getText();  
        String email = emailField.getText();  
        String company = companyField.getText();
        String info = infoField.getText();
        if(!validateData(email, username, password)) return;
        if(company.isEmpty()) return;

        try {
            Notification.create(Env.ADMIN_ID, "Talent Recruiter Application:\nName: "+username+"\nEmail: "+email+"\nCompany: "+company+"\nPassword hash: "+PasswordManager.hashPassword(password) + "\nApplication Info:\n"+info);   
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "Error requesting account, please try again later.\n\t"+e.getMessage());
        }
        handleHomeView();
    } 

    @FXML void handleTRSignUp() {
        String username = nameField.getText(); 
        String password = passwordField.getText();
        String company = companyField.getText();
        String email = emailField.getText();  
        if(!validateData(email, username, password)) return;
        if(company.isEmpty()) return;

        try {
            int id = TalentRecruiter.create(username, email, PasswordManager.hashPassword(password), company);
            mainController.loadHomeView(Env.ADMIN_ID);
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "An error occurred while signing up: \n\t"+e.getMessage());
            clearFields();
        }
    }

    @FXML private void handleAudSignUp() {
        // Get values from the input fields
        String username = nameField.getText(); 
        String password = passwordField.getText();  
        Gender gender = genderComboBox.getValue();
        List<Gender> genderRoles  = genderRoleComboBox.getCheckModel().getCheckedItems();
        int yoe;
        String email = emailField.getText();  
        if(!validateData(email, username, password)) return;
        
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

    private boolean validateData(String email, String username, String password) {
        if (username.isEmpty() || password.isEmpty()){
            mainController.showErrorAlert("Invalid Input", "Cannot have empty fields.");
            clearFields(); 
            return false;
        }
        if (email.isEmpty() || email.contains("@" ) == false || email.contains(".") == false){
            mainController.showErrorAlert("Invalid Input", "Must be a valid email address.");
            emailField.clear();
            return false; 
        }
        return true;
    }

    private void clearFields() {
        emailField.clear();
        nameField.clear();
        passwordField.clear();
        yoeField.clear();
        genderComboBox.getSelectionModel().clearSelection();
    }

    @Override public void setMainController(MainController mainController) { this.mainController = mainController; }
}

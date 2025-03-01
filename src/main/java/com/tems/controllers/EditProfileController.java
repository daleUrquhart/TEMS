package com.tems.controllers;

import java.sql.SQLException;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import com.tems.models.Auditionee;
import com.tems.models.Gender;
import com.tems.models.TalentRecruiter;
import com.tems.models.User;
import com.tems.util.PasswordManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

@SuppressWarnings("unused")
public class EditProfileController implements BaseController{
    
    private int id;
    private int TRId;
    private MainController mainController; 
    @FXML private VBox mainBox;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField companyField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Gender> genderComboBox;  
    @FXML CheckComboBox<Gender> genderRoleComboBox;
    @FXML private TextField yoeField;  

    public void update() {
        try {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            
            if (email.strip().isEmpty() || name.strip().isEmpty()){
                mainController.showErrorAlert("Invalid Input", "Cannot have empty fields.");
                clearFields();
                return;
            }
            if (email.contains("@" ) == false || email.contains(".") == false){
                mainController.showErrorAlert("Invalid Input", "Must be a valid email address.");
                emailField.clear();
                return; 
            }

            if(User.getById(id).getRole().equals("auditionee")) {
                // Aud 
                Auditionee auditionee = Auditionee.getById(id); 
                List<Gender> genderRoles  = genderRoleComboBox.getCheckModel().getCheckedItems();
                Gender gender = genderComboBox.getValue();
                int yoe;
                try{
                    auditionee.setYOE(Integer.parseInt(yoeField.getText()));
                } catch(NumberFormatException e) {
                    mainController.showErrorAlert("Error", "Bad YOE update");
                    return;
                }  
                if(!password.isEmpty()) auditionee.setPasswordHash(PasswordManager.hashPassword(password)); 
                auditionee.setGender(gender);
                auditionee.setGenderRoles(genderRoles);
                auditionee.update();
            } else {
                // TR
                TalentRecruiter tr = TalentRecruiter.getById(id);
                String company = companyField.getText();
                if(company.strip().isEmpty()) {
                    mainController.showErrorAlert("Error", "Must enter a value for comapny.");
                    return;
                }
                if(!password.isEmpty()) tr.setPasswordHash(PasswordManager.hashPassword(password)); 
                tr.setCompany(company);
                tr.update();
            }
            mainController.showErrorAlert("Success", "Account updated successfully");
            mainController.loadHomeView(TRId == 0 ? id : TRId);
        } catch(SQLException e) {
            mainController.showErrorAlert("Error", "Error loading user.\n\t"+e.getMessage());
        }
    }
    
    /**
     * Update current user's profile
     * @param id User ID
     */
    public void setUserData(int id) {
        this.id = id;
        try {
            User u = User.getById(id);
            nameField.setText(u.getName());
            emailField.setText(u.getEmail());
    
            if (u.getRole().equals("auditionee")) {
                loadAuditioneeComponents(id);
    
            } else {
                // Talent Recruiter-specific data
                TalentRecruiter tr = TalentRecruiter.getById(id);
                companyField.setText(tr.getCompany());
            }
        } catch (SQLException e) {
            mainController.showErrorAlert("Error", "Error loading edit user page. " + e.getMessage());
        }
    }    

    /**
     * Auditionee update being done by talent recruiter
     * @param TRId TR ID
     * @param aId Auditionee ID
     */
    public void setUserData(int TRId, int aId) { this.TRId = TRId; try{loadAuditioneeComponents(aId);} catch(SQLException e) {mainController.showErrorAlert("Error", "Error loading auditionee edit components: "+e.getMessage());}}

    public void loadAuditioneeComponents(int id) throws SQLException {
        // Auditionee-specific data
        genderComboBox.getItems().addAll(Gender.values());
    
        // Fetch auditionee data
        Auditionee a = Auditionee.getById(id);
        nameField.setText(a.getName());
        emailField.setText(a.getEmail());
        genderComboBox.setValue(a.getGender());
        yoeField.setText(Integer.toString(a.getYOE()));

        // Store all available gender roles
        ObservableList<Gender> allGenders = FXCollections.observableArrayList(Gender.getAll());
        genderRoleComboBox.getItems().setAll(allGenders);

        // Schedule selection update after UI is stable
        Platform.runLater(() -> {
            try{
            for (Gender g : a.getGenderRoles()) { 
                genderRoleComboBox.getCheckModel().check(g);
            }
            } catch(SQLException e) {mainController.showErrorAlert("Error", "Error laoding gender role data");}
        });
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void clearFields() {
        emailField.clear();
        nameField.clear();
        passwordField.clear();
        yoeField.clear();
        genderComboBox.getSelectionModel().clearSelection();
    }

    @FXML 
    public void handleHomeView() {
        mainController.loadHomeView(id);
    }
}

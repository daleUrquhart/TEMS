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
    
    private int editeeId = 0;
    private int editorId = 0;
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
            String editeeRole = User.getById(editeeId).getRole();
            // General data validation
            if (email.strip().isEmpty() || name.strip().isEmpty() || password.strip().isEmpty()){
                mainController.showErrorAlert("Invalid Input", "Cannot have empty fields.");
                return;
            }
            if (email.contains("@" ) == false || email.contains(".") == false){
                mainController.showErrorAlert("Invalid Input", "Must be a valid email address.");
                emailField.clear();
                return; 
            }

            if(editeeRole.equals("Admin")) {
                // Aud 
                Auditionee auditionee = Auditionee.getById(editeeId); 
                auditionee.setName(name);
                auditionee.setEmail(email);
                if(!password.strip().isEmpty()) auditionee.setPasswordHash(PasswordManager.hashPassword(password));
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
                TalentRecruiter tr = TalentRecruiter.getById(editeeId);
                tr.setName(name);
                tr.setEmail(email);
                if(!password.strip().isEmpty()) tr.setPasswordHash(PasswordManager.hashPassword(password));

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
            mainController.loadHomeView(editorId);
        } catch(SQLException e) {
            mainController.showErrorAlert("Error", "Error loading user.\n\t"+e.getMessage());
        }
    }
    
    /**
     * Update current user's profile
     * @param id User ID
     */
    public void setUserData(int id) {
        try {
            User u = User.getById(id);
            this.editeeId = id;
            this.editorId = id;
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
     * @param editorId TR ID
     * @param editeeId Auditionee ID
     */
    public void setUserData(int editorId, int editeeId) { 
        this.editorId = editorId;
        this.editeeId = editeeId; 
        try{
            User u = User.getById(editeeId);
            nameField.setText(u.getName());
            emailField.setText(u.getEmail());
            if(u.getRole().equals("auditionee")) loadAuditioneeComponents(editeeId);
            else {
                // Talent Recruiter-specific data
                TalentRecruiter tr = TalentRecruiter.getById(editeeId);
                companyField.setText(tr.getCompany());
            }
        } 
        catch(SQLException e) {mainController.showErrorAlert("Error", "Error loading auditionee edit components: "+e.getMessage());}}

    private void loadAuditioneeComponents(int id) throws SQLException {
        // Auditionee-specific data
        genderComboBox.getItems().addAll(Gender.values());
    
        // Fetch auditionee data
        Auditionee a = Auditionee.getById(id);
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

    @Override public void setMainController(MainController mainController) { this.mainController = mainController; }

    @FXML public void handleHomeView() { mainController.loadHomeView(editorId); }
}

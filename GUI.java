import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {

    private TableView<Employee> table;
    private ObservableList<Employee> employeeData;

    @Override
    public void start(Stage primaryStage) {
        // Initialize the database table
        DBHelper.createTableIfNotExists();

        // GUI components
        table = new TableView<>();
        employeeData = FXCollections.observableArrayList();
        loadEmployeesFromDB();

        TextField nameField = new TextField();
        nameField.setPromptText("Employee Name");
        TextField jobField = new TextField();
        jobField.setPromptText("Job Title");
        TextField salaryField = new TextField();
        salaryField.setPromptText("Salary");

        Button addButton = new Button("Add Employee");
        addButton.setOnAction(e -> {
            String name = nameField.getText();
            String job = jobField.getText();
            double salary = Double.parseDouble(salaryField.getText());

            // Insert into DB
            DBHelper.insertEmployee(name, job, salary);

            // Clear input fields
            nameField.clear();
            jobField.clear();
            salaryField.clear();

            // Refresh table
            loadEmployeesFromDB();
        });

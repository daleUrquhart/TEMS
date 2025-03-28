import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.formdev.flatlaf.FlatLightLaf; // Import FlatLaf

public class GUI extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, jobField, salaryField;

    public GUI() {
        setTitle("Employee Management");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Apply FlatLaf Theme
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Initialize DB
        DBHelper.createTableIfNotExists();

        // Table setup
        String[] columns = {"ID", "Name", "Job", "Salary"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Input Fields
        nameField = new JTextField(10);
        jobField = new JTextField(10);
        salaryField = new JTextField(7);

        JButton addButton = new JButton("Add Employee");
        addButton.addActionListener(e -> addEmployee());

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Job:"));
        inputPanel.add(jobField);
        inputPanel.add(new JLabel("Salary:"));
        inputPanel.add(salaryField);
        inputPanel.add(addButton);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        loadEmployeesFromDB();
    }

    private void addEmployee() {
        String name = nameField.getText();
        String job = jobField.getText();
        String salaryText = salaryField.getText();

        try {
            double salary = Double.parseDouble(salaryText);
            DBHelper.insertEmployee(name, job, salary);
            nameField.setText("");
            jobField.setText("");
            salaryField.setText("");
            loadEmployeesFromDB();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid salary.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEmployeesFromDB() {
        tableModel.setRowCount(0);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
    
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:employees.db");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Employee");
    
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("job"),
                    rs.getDouble("salary")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } 
        
    }
    

   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}

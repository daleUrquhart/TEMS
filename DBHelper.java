import java.sql.*;

public class DBHelper {
    private static final String DB_URL = "jdbc:sqlite:employees.db";

    // Ensure the SQLite driver is loaded
    static {
        try {
            Class.forName("org.sqlite.JDBC"); // For compatibility with older versions
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    
    public static void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Employee (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                job TEXT NOT NULL,
                salary REAL NOT NULL
            );
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
    public static void insertEmployee(String name, String job, double salary) {
        String sql = "INSERT INTO Employee (name, job, salary) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, job);
            pstmt.setDouble(3, salary);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
    public static ResultSet getEmployees() {
        String sql = "SELECT * FROM Employee";
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

   
    public static void closeConnection(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

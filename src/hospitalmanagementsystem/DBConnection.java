package hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DBConnection {
    
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String url;
    private static String user;
    private static String password;
    
    // Static block to load properties once when class is loaded
    static {
        Properties props = loadProperties();
        if (props != null) {
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        }

        // Try to load the MySQL JDBC driver explicitly
        try {
            Class.forName(DRIVER);
            System.out.println("MySQL JDBC driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver NOT found on classpath.");
            e.printStackTrace();
        }
    }
    
    /**
     * Load database properties from db.properties file
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getResourceAsStream("/hospitalmanagementsystem/db.properties")) {
            if (input == null) {
                System.out.println("Sorry, db.properties file not found in classpath!");
                return null;
            }
            props.load(input);
        } catch (IOException e) {
            System.out.println("Error reading db.properties file:");
            e.printStackTrace();
        }
        return props;
    }
    
    /**
     * Creates and returns a NEW connection each time
     * This allows try-with-resources to close it without affecting other operations
     */
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            System.out.println("Error message: " + e.getMessage());
            System.out.println("SQL state: " + e.getSQLState());
            System.out.println("Error code: " + e.getErrorCode());
            return null;
        }
    }
}
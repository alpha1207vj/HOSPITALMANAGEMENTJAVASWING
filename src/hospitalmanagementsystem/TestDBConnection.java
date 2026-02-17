package hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        // Open connection
        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println("DB connection works!");
        } else {
            System.out.println("DB connection failed.");
            return; // stop if connection failed
        }

        // Add a shutdown hook to close the connection when the program exits
        final Connection finalConn = conn;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (finalConn != null && !finalConn.isClosed()) {
                    finalConn.close();
                    System.out.println("\nConnection closed successfully.");
                }
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }));

        // Keep program running until user decides to exit
        System.out.println("Connection will remain open until you stop the program.");
        System.out.println("Press Ctrl+C to exit...");

        try {
            // Simple loop to keep the main thread alive
            while (true) {
                Thread.sleep(1000); // sleep 1 second
            }
        } catch (InterruptedException e) {
            System.out.println("Program interrupted!");
        } finally {
            // Close connection when program exits normally
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}

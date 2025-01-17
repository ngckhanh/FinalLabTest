package components;
/**
 * @author Ton Nu Ngoc Khanh - s3932105
 */
import components.databases.DatabaseConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // Launch the GUI
        RestaurantManagementSystem.launchGUI(args);

        // Database connection logic
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        // Get the connection object
        Connection connection = dbConnection.getConnection();

        try {
            if (connection != null) {
                System.out.println("Connection is active.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseConnection.initializeDatabase();

    }
}

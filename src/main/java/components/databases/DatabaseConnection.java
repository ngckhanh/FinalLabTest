package components.databases;
/**
 * @author Ton Nu Ngoc Khanh - s3932105
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private static Connection connection;
    private static final String url = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?user=postgres.drpxhqdjnldasbislbls&password=Kh@nh762003";
    private static final String username = "postgres.drpxhqdjnldasbislbls";
    private static final String password = "Kh@nh762003";

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to PostgresSQL successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database!", e);
        }
    }

    // Public method to get the Singleton instance
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Cai cua tui
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Reconnected to PostgresSQL successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to reconnect to the database!", e);
        }

        return connection;
    }

    public static void initializeDatabase() {
        String createCustomerTable = "CREATE TABLE IF NOT EXISTS customer (" +
                "id SERIAL PRIMARY KEY," +
                "name TEXT," +
                "address TEXT," +
                "phone_number TEXT);";

        String createDeliverymanTable = "CREATE TABLE IF NOT EXISTS deliveryman (" +
                "id SERIAL PRIMARY KEY," +
                "name TEXT," +
                "phone_number TEXT);";

        String createOrderTable = "CREATE TABLE IF NOT EXISTS orders (" +
                "id SERIAL PRIMARY KEY," +
                "total_price REAL," +
                "date TEXT," +
                "customer_id INTEGER," +
                "deliveryman_id INTEGER," +
                "FOREIGN KEY(customer_id) REFERENCES customer(id)," +
                "FOREIGN KEY(deliveryman_id) REFERENCES deliveryman(id));";

        String createItemTable = "CREATE TABLE IF NOT EXISTS item (" +
                "id SERIAL PRIMARY KEY," +
                "name TEXT," +
                "price REAL);";

        String createOrderItemTable = "CREATE TABLE IF NOT EXISTS order_item (" +
                "order_id INTEGER," +
                "item_id INTEGER," +
                "FOREIGN KEY(order_id) REFERENCES orders(id)," +
                "FOREIGN KEY(item_id) REFERENCES item(id));";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(createCustomerTable);
            stmt.execute(createDeliverymanTable);
            stmt.execute(createItemTable);
            stmt.execute(createOrderTable);
            stmt.execute(createOrderItemTable);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

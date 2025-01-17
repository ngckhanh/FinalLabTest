package components.controllers;
/**
 * @author Ton Nu Ngoc Khanh - s3932105
 */
import components.databases.DatabaseConnection;
import components.entities.Customer;
import components.entities.Deliveryman;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import components.entities.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DeliverymanController {
    public static ObservableList<Deliveryman> getAllDeliverymen() {
        String url = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?user=postgres.drpxhqdjnldasbislbls&password=Kh@nh762003";
        ObservableList<Deliveryman> deliverymanList = FXCollections.observableArrayList();
        String query = "SELECT d.*, o.id AS order_id " +
                "FROM deliveryman d " +
                "LEFT JOIN orders o ON d.id = o.deliveryman_id";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Use a map to group deliverymen by their ID
            Map<Integer, Deliveryman> deliverymanMap = new HashMap<>();

            // Iterate over the result set and create Deliveryman objects
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String phoneNumber = resultSet.getString("phone_number");

                // Check if the deliveryman already exists in the map
                Deliveryman deliveryman = deliverymanMap.get(id);
                if (deliveryman == null) {
                    // Create a new Deliveryman object if it doesn't exist
                    deliveryman = new Deliveryman();
                    deliveryman.setId(id);
                    deliveryman.setName(name);
                    deliveryman.setPhoneNumber(phoneNumber);
                    deliverymanMap.put(id, deliveryman); // Add to the map
                }

                // Get the order ID
                int orderId = resultSet.getInt("order_id");
                if (!resultSet.wasNull()) {
                    // There is an associated order
                    Order order = new Order(); // Create an Order object as needed
                    order.setId(orderId); // Set the order ID
                    deliveryman.addOrder(order); // Add the order to the deliveryman
                }
            }

            // Add all deliverymen from the map to the observable list
            deliverymanList.addAll(deliverymanMap.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deliverymanList;  // Return the ObservableList containing all deliverymen
    }

    public static void addDeliveryman(Deliveryman deliveryman) {
        String query = "INSERT INTO deliveryman (name, phone_number) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, deliveryman.getName());
            preparedStatement.setString(2, deliveryman.getPhoneNumber());

            // Execute the insert query
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDeliveryman(Deliveryman deliveryman) {
        String sql = "UPDATE deliveryman SET name = ?, phone_number = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, deliveryman.getName());
            pstmt.setString(2, deliveryman.getPhoneNumber());
            pstmt.setInt(3, deliveryman.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDeliveryman(int deliverymanId) {
        // Connection string, replace with your actual database credentials if needed
        String url = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?user=postgres.drpxhqdjnldasbislbls&password=Kh@nh762003";

        try (Connection con = DriverManager.getConnection(url)) {
            // Start a transaction
            con.setAutoCommit(false); // Disable auto-commit for transaction management

            try {
                // Step 1: Update orders to remove the deliveryman_id reference
                String updateOrderQuery = "UPDATE \"orders\" SET \"deliveryman_id\" = NULL WHERE \"deliveryman_id\" = ?";
                try (PreparedStatement stmt = con.prepareStatement(updateOrderQuery)) {
                    stmt.setInt(1, deliverymanId);
                    stmt.executeUpdate();
                }

                // Step 2: Delete the deliveryman from the deliveryman table
                String deleteDeliverymanQuery = "DELETE FROM \"deliveryman\" WHERE \"id\" = ?";
                try (PreparedStatement stmt = con.prepareStatement(deleteDeliverymanQuery)) {
                    stmt.setInt(1, deliverymanId);
                    stmt.executeUpdate();
                }

                // Commit the transaction after both steps
                con.commit();
                System.out.println("Deliveryman deleted successfully.");
            } catch (SQLException e) {
                // Rollback in case of an error during the transaction
                con.rollback();
                System.err.println("Transaction rolled back due to an error.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Deliveryman> searchDeliverymen(String keyword) {
        String sql = "SELECT * FROM deliveryman WHERE name LIKE ? OR phone_number LIKE ?";
        List<Deliveryman> deliverymen = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Deliveryman deliveryman = new Deliveryman();
                deliveryman.setId(rs.getInt("id"));
                deliveryman.setName(rs.getString("name"));
                deliveryman.setPhoneNumber(rs.getString("phone_number"));
                deliverymen.add(deliveryman);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deliverymen;
    }

    public static Deliveryman getDeliverymanById(int deliverymanId) {
        String query = "SELECT * FROM deliveryman WHERE id = ?";
        Deliveryman deliveryman = null;

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, deliverymanId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                deliveryman = new Deliveryman();
                deliveryman.setId(resultSet.getInt("id"));
                deliveryman.setName(resultSet.getString("name"));
                deliveryman.setPhoneNumber(resultSet.getString("phone_number"));

                // The orders list is initialized in the constructor and remains empty here.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deliveryman;
    }


}

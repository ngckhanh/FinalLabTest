package components.controllers;
/**
 * @author Ton Nu Ngoc Khanh - s3932105
 */
import components.databases.DatabaseConnection;
import components.entities.Customer;
import components.entities.Order;
import javafx.collections.*;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerController {
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        String query = "SELECT c.*, o.id AS order_id " +
                "FROM customer c " +
                "LEFT JOIN orders o ON c.id = o.customer_id";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Use a map to group customers by their ID
            Map<Integer, Customer> customerMap = new HashMap<>();

            // Iterate over the result set and create Customer objects
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String phone = resultSet.getString("phone_number");
                String address = resultSet.getString("address");

                // Check if the customer already exists in the map
                Customer customer = customerMap.get(id);
                if (customer == null) {
                    // Create a new Customer object if it doesn't exist
                    customer = new Customer(id, name, address, phone);
                    customerMap.put(id, customer); // Add to the map
                }

                // Get the order ID
                int orderId = resultSet.getInt("order_id");
                if (resultSet.wasNull()) {
                    // No associated order, do nothing
                } else {
                    Order order = new Order(); // Create an Order object as needed
                    order.setId(orderId); // Set the order ID
                    customer.addOrder(order); // Add the order to the customer
                }
            }

            // Add all customers from the map to the observable list
            customerList.addAll(customerMap.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerList;  // Return the ObservableList containing all customers
    }
    public static void addCustomer(String name, String address, String phoneNumber) {
        String query = "INSERT INTO customer (name, phone_number, address) VALUES (?, ?, ?)";

        try {
            // Get connection
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, phoneNumber);

            // Execute the insert query
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomer(int customerId, String name, String address, String phoneNumber) {
        String sql = "UPDATE customer SET name = ?, address = ?, phone_number = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, phoneNumber);
            pstmt.setInt(4, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(int customerId) {
        String sql = "DELETE FROM customer WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Customer> searchCustomers(String keyword, boolean orderDescending) {
        String order = orderDescending ? "DESC" : "ASC";
        String sql = "SELECT * FROM customer WHERE name LIKE ? OR phone_number LIKE ? ORDER BY name " + order;
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customer.setPhoneNumber(rs.getString("phone_number"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static Customer getCustomerById(int customerId) {
        String query = "SELECT * FROM customer WHERE id = ?";
        Customer customer = null;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Create a Customer object from the result set
                customer = new Customer();
                customer.setId(resultSet.getInt("id"));
                customer.setName(resultSet.getString("name"));
                customer.setAddress(resultSet.getString("address"));
                customer.setPhoneNumber(resultSet.getString("phone_number"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customer; // Return the Customer object or null if not found
    }
}

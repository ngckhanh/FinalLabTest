package components.controllers;

import components.databases.DatabaseConnection;
import components.entities.Customer;
import components.entities.Deliveryman;
import components.entities.Item;
import components.entities.Order;
import javafx.collections.*;
import javafx.scene.control.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static components.databases.DatabaseConnection.url;

public class OrderController {
    public static ObservableList<Order> getAllOrders() {
        ObservableList<Order> orderList = FXCollections.observableArrayList();
        String query = "SELECT o.id AS order_id, o.total_price, o.date, o.customer_id, o.deliveryman_id, oi.item_id " +
                "FROM orders o " +
                "JOIN order_item oi ON o.id = oi.order_id";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Use a map to group items by order ID
            Map<Integer, Order> orderMap = new HashMap<>();

            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                double totalPrice = resultSet.getDouble("total_price");
                Date creationDate = resultSet.getDate("date");
                int customerId = resultSet.getInt("customer_id");
                int deliverymanId = resultSet.getInt("deliveryman_id");
                int itemId = resultSet.getInt("item_id");

                // Check if the order already exists in the map
                Order order = orderMap.get(orderId);
                if (order == null) {
                    // Create a new Order object if it doesn't exist
                    order = new Order();
                    order.setId(orderId);
                    order.setTotalPrice(totalPrice);
                    order.setCreationDate(creationDate);

                    // Fetch the customer and deliveryman
                    Customer customer = CustomerController.getCustomerById(customerId);
                    order.setCustomer(customer);

                    Deliveryman deliveryman = DeliverymanController.getDeliverymanById(deliverymanId);
                    order.setDeliveryman(deliveryman);

                    // Initialize the items list
                    order.setItems(new ArrayList<>());

                    // Add the order to the map
                    orderMap.put(orderId, order);
                }

                // Fetch the item and add it to the order's items list
                Item item = ItemController.getItemById(itemId);
                if (item != null) {
                    order.getItems().add(item);
                }
            }

            // Add all orders from the map to the observable list
            orderList.addAll(orderMap.values());

        } catch (SQLException e) {
            System.err.println("SQL Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return orderList;  // Return the ObservableList containing all orders
    }

    public static void addOrder(Order order) {
        String query = "INSERT INTO orders (total_price, date, customer_id, deliveryman_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setDouble(1, order.getTotalPrice());
            preparedStatement.setDate(2, new java.sql.Date(order.getCreationDate().getTime()));
            preparedStatement.setInt(3, order.getCustomer().getId());
            preparedStatement.setInt(4, order.getDeliveryman().getId());

            // Execute the insert query
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the generated order ID
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1); // Get the generated order ID

                        // Now insert items into the order_item table
                        String itemOrder = "INSERT INTO order_item (order_id, item_id) VALUES (?, ?)";
                        try (PreparedStatement itemPreparedStatement = connection.prepareStatement(itemOrder)) {
                            for (Item item : order.getItems()) {
                                itemPreparedStatement.setInt(1, orderId); // Use the generated order ID
                                itemPreparedStatement.setInt(2, item.getId()); // Use the item's ID
                                itemPreparedStatement.executeUpdate(); // Execute the insert for each item
                            }
                        }
                    } else {
                        throw new SQLException("Failed to retrieve order ID.");
                    }
                }
            } else {
                throw new SQLException("No rows affected, order not added.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging this instead of printing
        }
    }

    public static void updateOrder(Order order) {
        String updateOrderQuery = "UPDATE orders SET total_price = ?, date = ?, customer_id = ?, deliveryman_id = ? WHERE id = ?";
        String deleteOrderItemsQuery = "DELETE FROM order_item WHERE order_id = ?";
        String insertOrderItemsQuery = "INSERT INTO order_item (order_id, item_id) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(url)) {
            // Start a transaction
            connection.setAutoCommit(false);

            try {
                // Step 1: Update the order in the orders table
                try (PreparedStatement updateStmt = connection.prepareStatement(updateOrderQuery)) {
                    updateStmt.setDouble(1, order.getTotalPrice());
                    updateStmt.setDate(2, new java.sql.Date(order.getCreationDate().getTime()));
                    updateStmt.setInt(3, order.getCustomer().getId());
                    updateStmt.setInt(4, order.getDeliveryman().getId());
                    updateStmt.setInt(5, order.getId());
                    updateStmt.executeUpdate();
                }

                // Step 2: Delete all existing items for the order in the order_item table
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteOrderItemsQuery)) {
                    deleteStmt.setInt(1, order.getId());
                    deleteStmt.executeUpdate();
                }

                // Step 3: Re-insert the updated items into the order_item table
                try (PreparedStatement insertStmt = connection.prepareStatement(insertOrderItemsQuery)) {
                    for (Item item : order.getItems()) {
                        insertStmt.setInt(1, order.getId()); // Use the existing order ID
                        insertStmt.setInt(2, item.getId()); // Use the item's ID
                        insertStmt.executeUpdate();
                    }
                }

                // Commit the transaction
                connection.commit();
                System.out.println("Order updated successfully.");
            } catch (SQLException e) {
                // Rollback the transaction in case of an error
                connection.rollback();
                System.err.println("Transaction rolled back due to an error.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrder(int orderId) {
        try (Connection con = DriverManager.getConnection(url)) {
            // Start a transaction
            con.setAutoCommit(false);

            try {
                // Step 1: Delete references from the order_item table
                String deleteOrderItemQuery = "DELETE FROM \"order_item\" WHERE \"order_id\" = ?";
                try (PreparedStatement stmt = con.prepareStatement(deleteOrderItemQuery)) {
                    stmt.setInt(1, orderId);
                    stmt.executeUpdate();
                }

                // Step 2: Delete the order from the Order table
                String deleteOrderQuery = "DELETE FROM \"orders\" WHERE \"id\" = ?";
                try (PreparedStatement stmt = con.prepareStatement(deleteOrderQuery)) {
                    stmt.setInt(1, orderId);
                    stmt.executeUpdate();
                }

                // Commit the transaction
                con.commit();
                System.out.println("Order deleted successfully.");
            } catch (SQLException e) {
                // Rollback if any step fails
                con.rollback();
                System.err.println("Transaction rolled back due to an error.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> searchOrders(String keyword) {
        String sql = "SELECT * FROM orders WHERE total_price LIKE ?"; // Adjust based on your search criteria
        List<Order> orders = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setCreationDate(rs.getDate("creation_date"));
                // You may want to fetch the customer and set it here
                // Customer customer = CustomerController.getCustomerById(rs.getInt("customer_id"));
                // order.setCustomer(customer);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}


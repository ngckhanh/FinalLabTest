package components.controllers;

import components.databases.DatabaseConnection;
import components.entities.Customer;
import components.entities.Item;
import components.entities.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemController {

    public static ObservableList<Item> getAllItems() {
        ObservableList<Item> itemList = FXCollections.observableArrayList();
        String query = "SELECT * FROM item";  // SQL query to get all items

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?user=postgres.drpxhqdjnldasbislbls&password=Kh@nh762003");
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iterate over the result set and create Item objects
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");

                // Create an Item object
                Item item = new Item(id, name, price);
                itemList.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itemList;  // Return the ObservableList containing all items
    }

    public static void addItem(Item item) {
        String query = "INSERT INTO item (name, price) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, item.getName());
            preparedStatement.setDouble(2, item.getPrice());

            // Execute the insert query
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateItem(Item item) {
        String sql = "UPDATE item SET name = ?, price = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setInt(3, item.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public void deleteItem(int itemId) {
//        // Check if the item is referenced in the order_item table
//        String checkSql = "SELECT COUNT(*) FROM order_item WHERE item_id = ?";
//        try (Connection connection = DatabaseConnection.getInstance().getConnection();
//             PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
//
//            checkStmt.setInt(1, itemId);
//            ResultSet rs = checkStmt.executeQuery();
//            if (rs.next() && rs.getInt(1) > 0) {
//                // Item is referenced, retrieve associated orders
//                List<Order> associatedOrders = getOrdersByItemId(itemId);
//                for (Order order : associatedOrders) {
//                    // Assuming you have a method to get the customer by order
//                    Customer customer = getCustomerByOrder(order);
//                    if (customer != null) {
//                        customer.removeOrder(order); // Remove the order from the customer
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        // Proceed to delete the item
//        String sql = "DELETE FROM item WHERE id = ?";
//        try (Connection connection = DatabaseConnection.getInstance().getConnection();
//             PreparedStatement pstmt = connection.prepareStatement(sql)) {
//
//            pstmt.setInt(1, itemId);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public void deleteItem(int itemId) {
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?user=postgres.drpxhqdjnldasbislbls&password=Kh@nh762003")) {
            // Start a transaction
            con.setAutoCommit(false);

            try {
                // Step 1: Delete references from the order_item table
                String deleteOrderItemQuery = "DELETE FROM \"order_item\" WHERE \"item_id\" = ?";
                try (PreparedStatement stmt = con.prepareStatement(deleteOrderItemQuery)) {
                    stmt.setInt(1, itemId);
                    stmt.executeUpdate();
                }

                // Step 2: Delete the item from the Item table
                String deleteItemQuery = "DELETE FROM \"item\" WHERE \"id\" = ?";
                try (PreparedStatement stmt = con.prepareStatement(deleteItemQuery)) {
                    stmt.setInt(1, itemId);
                    stmt.executeUpdate();
                }

                // Commit the transaction
                con.commit();
                System.out.println("Item deleted successfully.");
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

    // Method to retrieve orders associated with an item
    private List<Order> getOrdersByItemId(int itemId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.* FROM orders o " +
                "JOIN order_item oi ON o.id = oi.order_id " +
                "WHERE oi.item_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setCreationDate(rs.getDate("date"));
                // Set other order properties as needed
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Method to retrieve the customer associated with an order
    private Customer getCustomerByOrder(Order order) {
        Customer customer = null;
        String query = "SELECT c.id, c.name, c.address, c.phone_number " +
                "FROM orders o " +
                "JOIN customer c ON c.id = o.customer_id " +
                "WHERE o.id = ?"; // Use the order ID to filter

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, order.getId()); // Set the order ID as a parameter
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                customer = new Customer();
                customer.setId(rs.getInt("id")); // Get customer ID
                customer.setName(rs.getString("name")); // Get customer name
                customer.setAddress(rs.getString("address")); // Get customer address
                customer.setPhoneNumber(rs.getString("phone_number")); // Get customer phone number
                // Set other customer properties as needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer; // Return the Customer object or null if not found
    }

    public List<Item> searchItems(String keyword) {
        String sql = "SELECT * FROM item WHERE name LIKE ?";
        List<Item> items = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static Item getItemById(int itemId) {
        Item item = null;
        String query = "SELECT * FROM item WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, itemId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Create an Item object and populate it with data from the result set
                item = new Item();
                item.setId(resultSet.getInt("id"));
                item.setName(resultSet.getString("name"));
                item.setPrice(resultSet.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return item; // Return the Item object or null if not found
    }

    public Item getItemByName(String name) {
        String query = "SELECT * FROM item WHERE name = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                double price = resultSet.getDouble("price");

                // Return the Item object
                return new Item(id, name, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Return null if no item is found
    }


}
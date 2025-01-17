package components.entities;
/**
 * @author Ton Nu Ngoc Khanh
 */

import java.util.ArrayList;
import java.util.List;

public class Deliveryman {
    private int id;
    private String name;
    private String phoneNumber;
    private List<Order> orders;

    // Constructors
    public Deliveryman() {
        orders = new ArrayList<>();
    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public List<Order> getOrders() { return orders; }

    public void addOrder(Order order) {
        orders.add(order);
        order.setDeliveryman(this); // Maintain bi-directional relationship
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setDeliveryman(null); // Maintain bi-directional relationship
    }

    @Override
    public String toString() {
        StringBuilder ordersId = new StringBuilder();
        for (Order order : orders) {
            ordersId.append(order.getId()).append(", ");
        }
        // Remove the last comma and space if there are items
        if (ordersId.length() > 0) {
            ordersId.setLength(ordersId.length() - 2); // Remove last comma and space
        }

        return "Deliveryman Details: " +
                "ID: " + id + ", " +
                "Name: " + name + ", " +
                "Phone: " + phoneNumber + ", " +
                "Orders: " + (ordersId != null ? ordersId : "null") + "\n";
    }
}


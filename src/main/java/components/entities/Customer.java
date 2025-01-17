package components.entities;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private List<Order> orders = new ArrayList<>();

    // Constructors
    public Customer() {}

    public Customer(int id, String name, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public List<Order> getOrders() { return orders; }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setCustomer(this); // Maintain bi-directional relationship
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setCustomer(null); // Maintain bi-directional relationship
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

        return "Customer Details: " +
                "ID: " + id + ", " +
                "Name: " + name + ", " +
                "Address: " + address + ", " +
                "Phone Number: " + phoneNumber + ", " +
                "Orders: " + (ordersId != null ? ordersId : "null") + "\n";
    }
}

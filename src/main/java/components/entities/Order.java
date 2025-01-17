package components.entities;
// Order.java
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private double totalPrice;
    private Date creationDate;
    private List<Item> items;
    private Deliveryman deliveryman;
    private Customer customer;

    // Constructors
    public Order() {
        items = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public List<Item> getItems() { return items; }

    public void setItems(List<Item> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }


    public Deliveryman getDeliveryman() { return deliveryman; }
    public void setDeliveryman(Deliveryman deliveryman) { this.deliveryman = deliveryman; }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        StringBuilder itemNames = new StringBuilder();
        for (Item item : items) {
            itemNames.append(item.getName()).append(", ");
        }
        // Remove the last comma and space if there are items
        if (itemNames.length() > 0) {
            itemNames.setLength(itemNames.length() - 2); // Remove last comma and space
        }

        return "Order Details: " +
                "ID: " + id + ", " +
                "Price: " + totalPrice + ", " +
                "Date: " + creationDate + ", " +
                "Items: " + itemNames.toString() + ", " +
                "Customer: " + (customer != null ? customer.getName() : "N/A") + ", " +
                "Deliveryman: " + (deliveryman != null ? deliveryman.getName() : "N/A") + "\n";
    }
}


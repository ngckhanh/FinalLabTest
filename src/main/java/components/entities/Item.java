package components.entities;


public class Item {
    private int id;
    private String name;
    private double price;

    // Constructors
    public Item() {}

    public Item(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Item Details: " +
                "ID: " + id + ", " +
                "Name: " + name + ", " +
                "Price: " + price + "\n";
    }
}



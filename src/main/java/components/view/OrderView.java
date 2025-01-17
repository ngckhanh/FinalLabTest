package components.view;

import components.entities.Item;
import components.entities.Order;
import components.entities.Customer;
import components.entities.Deliveryman;
import components.controllers.*;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class OrderView {
    private static final OrderController orderController = new OrderController();
    private static final CustomerController customerController = new CustomerController();
    private static final DeliverymanController deliverymanController = new DeliverymanController();
    private static final ItemController itemController = new ItemController();

    public static VBox getOrderView() {
        // Main container
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));

        // Section 1: Order Input Fields
        TitledPane inputPane = new TitledPane();
        inputPane.setText("Order Details");
        inputPane.setCollapsible(false);

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));

        Label idLabel = new Label("Order ID: ");
        TextField idEdit = new TextField();
        inputGrid.add(idLabel, 0, 0);
        inputGrid.add(idEdit, 1, 0);

        Label totalPriceLabel = new Label("Total Price: ");
        TextField totalPriceEdit = new TextField();
        inputGrid.add(totalPriceLabel, 0, 1);
        inputGrid.add(totalPriceEdit, 1, 1);

        Label creationDateLabel = new Label("Creation Date: ");
        DatePicker creationDatePicker = new DatePicker();
        inputGrid.add(creationDateLabel, 0, 2);
        inputGrid.add(creationDatePicker, 1, 2);

        Label itemListLabel = new Label("Items: ");
        ListView<String> itemListView = new ListView<>();
        itemListView.setPrefHeight(100);

        HBox itemControlBox = new HBox(10);
        TextField newItemField = new TextField();
        newItemField.setPromptText("Enter item name");
        Button addItemButton = new Button("Add Item");
        Button removeItemButton = new Button("Remove Selected");
        itemControlBox.getChildren().addAll(newItemField, addItemButton, removeItemButton);
        itemControlBox.setAlignment(Pos.CENTER_LEFT);

        VBox itemBox = new VBox(5, itemListView, itemControlBox);
        inputGrid.add(itemListLabel, 0, 3);
        inputGrid.add(itemBox, 1, 3);

        Label customerIdLabel = new Label("Customer ID: ");
        TextField customerIdEdit = new TextField();
        inputGrid.add(customerIdLabel, 0, 4);
        inputGrid.add(customerIdEdit, 1, 4);

        Label deliverymanIdLabel = new Label("Deliveryman ID: ");
        TextField deliverymanIdEdit = new TextField();
        inputGrid.add(deliverymanIdLabel, 0, 5);
        inputGrid.add(deliverymanIdEdit, 1, 5);

        inputPane.setContent(inputGrid);

        // Section 2: Search and Sort
        TitledPane searchPane = new TitledPane();
        searchPane.setText("Search Orders");
        searchPane.setCollapsible(false);

        HBox searchBox = new HBox(15);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10));

        Label searchLabel = new Label("Search (Total Price): ");
        TextField searchEdit = new TextField();
        Button searchButton = new Button("Search");

        searchBox.getChildren().addAll(searchLabel, searchEdit, searchButton);
        searchPane.setContent(searchBox);

        // Section 3: Action Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        Button addButton = new Button("Add Order");
        Button updateButton = new Button("Update Order");
        Button deleteButton = new Button("Delete Order");
        Button getAllButton = new Button("Get All Orders");

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, getAllButton);

        // Section 4: Result Area
        TitledPane resultPane = new TitledPane();
        resultPane.setText("Results");
        resultPane.setCollapsible(false);

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultPane.setContent(resultArea);

        // Status Label for error messages
        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);
        statusLabel.setPadding(new Insets(5));

        // Button Actions
        addItemButton.setOnAction(e -> {
            String newItem = newItemField.getText().trim();
            if (!newItem.isEmpty()) {
                itemListView.getItems().add(newItem);
                newItemField.clear();
            }
        });

        removeItemButton.setOnAction(e -> {
            String selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                itemListView.getItems().remove(selectedItem);
            }
        });

        addButton.setOnAction(e -> {
            if (totalPriceEdit.getText().isEmpty() || creationDatePicker.getValue() == null || customerIdEdit.getText().isEmpty() || deliverymanIdEdit.getText().isEmpty()) {
                statusLabel.setText("All fields are required to add an order.");
            } else {
                Order order = new Order();
                order.setTotalPrice(Double.parseDouble(totalPriceEdit.getText()));
                order.setCreationDate(Date.valueOf(creationDatePicker.getValue()));

                List<Item> items = itemListView.getItems()
                        .stream()
                        .map(itemName -> itemController.getItemByName(itemName)) // Retrieve full Item object by name
                        .filter(Objects::nonNull)
                        .toList();
                order.setItems(items);

                Customer customer = customerController.getCustomerById(Integer.parseInt(customerIdEdit.getText()));
                Deliveryman deliveryman = deliverymanController.getDeliverymanById(Integer.parseInt(deliverymanIdEdit.getText()));

                if (customer == null || deliveryman == null) {
                    statusLabel.setText("Invalid Customer ID or Deliveryman ID.");
                    return;
                }

                order.setCustomer(customer);
                order.setDeliveryman(deliveryman);
                orderController.addOrder(order);
                resultArea.setText(orderController.getAllOrders().toString());
                statusLabel.setText("Order added successfully.");
            }
        });

        getAllButton.setOnAction(e -> {
            resultArea.setText(orderController.getAllOrders().toString());
            statusLabel.setText("");
        });

        // Assemble the UI
        vbox.getChildren().addAll(inputPane, searchPane, buttonBox, resultPane, statusLabel);

        return vbox;
    }
}

package components.view;
/**
 * @author Ton Nu Ngoc Khanh - s3932105
 */
import components.controllers.ItemController;
import components.entities.Customer;
import components.entities.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;


public class ItemView {
    private static final ItemController itemController = new ItemController();

    public static VBox getItemView() {
        // Main container
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));

        // Section 1: Item Input Fields
        TitledPane inputPane = new TitledPane();
        inputPane.setText("Item Details");
        inputPane.setCollapsible(false);

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));

        Label idLabel = new Label("Item ID: ");
        TextField idEdit = new TextField();
        inputGrid.add(idLabel, 0, 0);
        inputGrid.add(idEdit, 1, 0);

        Label nameLabel = new Label("Name: ");
        TextField nameEdit = new TextField();
        inputGrid.add(nameLabel, 0, 1);
        inputGrid.add(nameEdit, 1, 1);

        Label priceLabel = new Label("Price: ");
        TextField priceEdit = new TextField();
        inputGrid.add(priceLabel, 0, 2);
        inputGrid.add(priceEdit, 1, 2);

        inputPane.setContent(inputGrid);

        // Section 2: Search
        TitledPane searchPane = new TitledPane();
        searchPane.setText("Search Items");
        searchPane.setCollapsible(false);

        HBox searchBox = new HBox(15);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10));

        Label searchLabel = new Label("Search (Name): ");
        TextField searchEdit = new TextField();
        Button searchButton = new Button("Search");

        searchBox.getChildren().addAll(searchLabel, searchEdit, searchButton);
        searchPane.setContent(searchBox);

        // Section 3: Action Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        Button addButton = new Button("Add Item");
        Button updateButton = new Button("Update Item");
        Button deleteButton = new Button("Delete Item");
        Button getAllButton = new Button("Get All Items");

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
        addButton.setOnAction(e -> {
            if (nameEdit.getText().isEmpty() || priceEdit.getText().isEmpty()) {
                statusLabel.setText("Name and Price fields are required to add an item.");
            } else {
                Item item = new Item();
                item.setName(nameEdit.getText());
                item.setPrice(Double.parseDouble(priceEdit.getText()));
                itemController.addItem(item);
                resultArea.setText(itemController.getAllItems().toString());
                statusLabel.setText("");
            }
        });

        updateButton.setOnAction(e -> {
            if (idEdit.getText().isEmpty() || nameEdit.getText().isEmpty() || priceEdit.getText().isEmpty()) {
                statusLabel.setText("Item ID, Name, and Price fields are required to update an item.");
            } else {
                Item item = new Item();
                item.setId(Integer.parseInt(idEdit.getText()));
                item.setName(nameEdit.getText());
                item.setPrice(Double.parseDouble(priceEdit.getText()));
                itemController.updateItem(item);
                resultArea.setText(itemController.getAllItems().toString());
                statusLabel.setText("");
            }
        });

        deleteButton.setOnAction(e -> {
            if (idEdit.getText().isEmpty()) {
                statusLabel.setText("Item ID field is required to delete an item.");
            } else {
                itemController.deleteItem(Integer.parseInt(idEdit.getText()));
                resultArea.setText(itemController.getAllItems().toString());
                statusLabel.setText("");
            }
        });

        searchButton.setOnAction(e -> {
            List<Item> result = itemController.searchItems(searchEdit.getText());
            resultArea.setText(result.toString());
            statusLabel.setText("");
        });

        getAllButton.setOnAction(e -> {
            resultArea.setText(itemController.getAllItems().toString());
            statusLabel.setText("");
        });

        // Assemble the UI
        vbox.getChildren().addAll(inputPane, searchPane, buttonBox, resultPane, statusLabel);

        return vbox;
    }
}

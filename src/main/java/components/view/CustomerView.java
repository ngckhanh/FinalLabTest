package components.view;
/**
 * @author Ton Nu Ngoc Khanh - s3932105
 */
import components.entities.Customer;
import components.controllers.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

public class CustomerView {
    private static final CustomerController controller = new CustomerController();

    public static VBox getCustomerView() {
        // Main container
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));

        // Section 1: Customer Input Fields
        TitledPane inputPane = new TitledPane();
        inputPane.setText("Customer Details");
        inputPane.setCollapsible(false);

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));

        Label idLabel = new Label("ID: ");
        TextField idEdit = new TextField();
        inputGrid.add(idLabel, 0, 0);
        inputGrid.add(idEdit, 1, 0);

        Label nameLabel = new Label("Name: ");
        TextField nameEdit = new TextField();
        inputGrid.add(nameLabel, 0, 1);
        inputGrid.add(nameEdit, 1, 1);

        Label addressLabel = new Label("Address: ");
        TextField addressEdit = new TextField();
        inputGrid.add(addressLabel, 0, 2);
        inputGrid.add(addressEdit, 1, 2);

        Label phoneLabel = new Label("Phone: ");
        TextField phoneEdit = new TextField();
        inputGrid.add(phoneLabel, 0, 3);
        inputGrid.add(phoneEdit, 1, 3);

        inputPane.setContent(inputGrid);

        // Section 2: Search and Sort
        TitledPane searchPane = new TitledPane();
        searchPane.setText("Search and Sort");
        searchPane.setCollapsible(false);

        HBox searchBox = new HBox(15);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10));

        Label searchLabel = new Label("Search (Name or Phone): ");
        TextField searchEdit = new TextField();
        Label orderLabel = new Label("Descending: ");
        CheckBox descending = new CheckBox();
        Button searchButton = new Button("Search");

        searchBox.getChildren().addAll(searchLabel, searchEdit, orderLabel, descending, searchButton);
        searchPane.setContent(searchBox);

        // Section 3: Action Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        Button addButton = new Button("Add Customer");
        Button updateButton = new Button("Update Customer");
        Button deleteButton = new Button("Delete Customer");
        Button getAllButton = new Button("Get All Customers");

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
            if (nameEdit.getText().isEmpty() || addressEdit.getText().isEmpty() || phoneEdit.getText().isEmpty()) {
                statusLabel.setText("Name, Address, and Phone fields are required to add a customer.");
            } else {
                controller.addCustomer(nameEdit.getText(), addressEdit.getText(), phoneEdit.getText());
                resultArea.setText(controller.getAllCustomers().toString());
                statusLabel.setText("");
            }
        });

        updateButton.setOnAction(e -> {
            if (idEdit.getText().isEmpty() || nameEdit.getText().isEmpty() || addressEdit.getText().isEmpty() || phoneEdit.getText().isEmpty()) {
                statusLabel.setText("ID, Name, Address, and Phone fields are required to update a customer.");
            } else {
                controller.updateCustomer(Integer.parseInt(idEdit.getText()), nameEdit.getText(), addressEdit.getText(), phoneEdit.getText());
                resultArea.setText(controller.getAllCustomers().toString());
                statusLabel.setText("");
            }
        });

        deleteButton.setOnAction(e -> {
            if (idEdit.getText().isEmpty()) {
                statusLabel.setText("ID field is required to delete a customer.");
            } else {
                controller.deleteCustomer(Integer.parseInt(idEdit.getText()));
                resultArea.setText(controller.getAllCustomers().toString());
                statusLabel.setText("");
            }
        });

        searchButton.setOnAction(e -> {
            List<Customer> result = controller.searchCustomers(searchEdit.getText(), descending.isSelected());
            resultArea.setText(result.toString());
            statusLabel.setText("");
        });

        getAllButton.setOnAction(e -> {
            resultArea.setText(controller.getAllCustomers().toString());
            statusLabel.setText("");
        });

        // Assemble the UI
        vbox.getChildren().addAll(inputPane, searchPane, buttonBox, resultPane, statusLabel);

        return vbox;
    }
}

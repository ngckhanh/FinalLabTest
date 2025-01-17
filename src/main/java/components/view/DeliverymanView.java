package components.view;
/**
 * @author Ton Nu Ngoc Khanh - s3932105
 */

import components.controllers.DeliverymanController;
import components.entities.Customer;
import components.entities.Deliveryman;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.List;


public class DeliverymanView {
    private static final DeliverymanController deliverymanController = new DeliverymanController();

    public static VBox getDeliverymanView() {
        // Main container
        VBox vbox = new VBox(20);
        vbox .setPadding(new Insets(20));

        // Section 1: Deliveryman Input Fields
        TitledPane inputPane = new TitledPane();
        inputPane.setText("Deliveryman Details");
        inputPane.setCollapsible(false);

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));

        Label idLabel = new Label("Deliveryman ID: ");
        TextField idEdit = new TextField();
        inputGrid.add(idLabel, 0, 0);
        inputGrid.add(idEdit, 1, 0);

        Label nameLabel = new Label("Name: ");
        TextField nameEdit = new TextField();
        inputGrid.add(nameLabel, 0, 1);
        inputGrid.add(nameEdit, 1, 1);

        Label phoneLabel = new Label("Phone Number: ");
        TextField phoneEdit = new TextField();
        inputGrid.add(phoneLabel, 0, 2);
        inputGrid.add(phoneEdit, 1, 2);

        inputPane.setContent(inputGrid);

        // Section 2: Search
        TitledPane searchPane = new TitledPane();
        searchPane.setText("Search Deliverymen");
        searchPane.setCollapsible(false);

        HBox searchBox = new HBox(15);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10));

        Label searchLabel = new Label("Search (Name/Phone): ");
        TextField searchEdit = new TextField();
        Button searchButton = new Button("Search");

        searchBox.getChildren().addAll(searchLabel, searchEdit, searchButton);
        searchPane.setContent(searchBox);

        // Section 3: Action Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        Button addButton = new Button("Add Deliveryman");
        Button updateButton = new Button("Update Deliveryman");
        Button deleteButton = new Button("Delete Deliveryman");
        Button getAllButton = new Button("Get All Deliverymen");

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
            if (nameEdit.getText().isEmpty() || phoneEdit.getText().isEmpty()) {
                statusLabel.setText("Name and Phone Number fields are required to add a deliveryman.");
            } else {
                Deliveryman deliveryman = new Deliveryman();
                deliveryman.setName(nameEdit.getText());
                deliveryman.setPhoneNumber(phoneEdit.getText());
                deliverymanController.addDeliveryman(deliveryman);
                resultArea.setText(deliverymanController.getAllDeliverymen().toString());
                statusLabel.setText("");
            }
        });

        updateButton.setOnAction(e -> {
            if (idEdit.getText().isEmpty() || nameEdit.getText().isEmpty() || phoneEdit.getText().isEmpty()) {
                statusLabel.setText("Deliveryman ID, Name, and Phone Number fields are required to update a deliveryman.");
            } else {
                Deliveryman deliveryman = new Deliveryman();
                deliveryman.setId(Integer.parseInt(idEdit.getText()));
                deliveryman.setName(nameEdit.getText());
                deliveryman.setPhoneNumber(phoneEdit.getText());
                deliverymanController.updateDeliveryman(deliveryman);
                resultArea.setText(deliverymanController.getAllDeliverymen().toString());
                statusLabel.setText("");
            }
        });

        deleteButton.setOnAction(e -> {
            if (idEdit.getText().isEmpty()) {
                statusLabel.setText("Deliveryman ID field is required to delete a deliveryman.");
            } else {
                deliverymanController.deleteDeliveryman(Integer.parseInt(idEdit.getText()));
                resultArea.setText(deliverymanController.getAllDeliverymen().toString());
                statusLabel.setText("");
            }
        });

        searchButton.setOnAction(e -> {
            List<Deliveryman> result = deliverymanController.searchDeliverymen(searchEdit.getText());
            resultArea.setText(result.toString());
            statusLabel.setText("");
        });

        getAllButton.setOnAction(e -> {
            resultArea.setText(deliverymanController.getAllDeliverymen().toString());
            statusLabel.setText("");
        });

        // Assemble the UI
        vbox.getChildren().addAll(inputPane, searchPane, buttonBox, resultPane, statusLabel);

        return vbox;
    }
}

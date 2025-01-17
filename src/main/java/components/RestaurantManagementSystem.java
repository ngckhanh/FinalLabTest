package components;
/**
 * @author Ton Nu Ngoc Khanh - s3932105
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import components.view.CustomerView;
import components.view.OrderView;
import components.view.ItemView;
import components.view.DeliverymanView;

public class RestaurantManagementSystem extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create tabs for each view
        TabPane tabPane = new TabPane();

        // Customer Tab
        Tab customerTab = new Tab("Manage Customers");
        customerTab.setContent(CustomerView.getCustomerView());

        // Order Tab
        Tab orderTab = new Tab("Manage Orders");
        orderTab.setContent(OrderView.getOrderView());

        // Item Tab
        Tab itemTab = new Tab("Manage Items");
        itemTab.setContent(ItemView.getItemView());

        // Deliveryman Tab
        Tab deliverymanTab = new Tab("Manage Deliverymen");
        deliverymanTab.setContent(DeliverymanView.getDeliverymanView());

        // Add tabs to the TabPane
        tabPane.getTabs().addAll(customerTab, orderTab, itemTab, deliverymanTab);

        // Set the tab to not be closable
        customerTab.setClosable(false);
        orderTab.setClosable(false);
        itemTab.setClosable(false);
        deliverymanTab.setClosable(false);

        // Create and set the scene
        Scene scene = new Scene(tabPane, 1000, 800);
        primaryStage.setTitle("Restaurant Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void launchGUI(String[] args) {
        launch(args);
    }
}

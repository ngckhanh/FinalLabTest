module org.example.finallabtest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    exports components.entities;
    opens components.entities to javafx.fxml;
    exports components.controllers;
    opens components.controllers to javafx.fxml;
    exports components;
    opens components to javafx.fxml;
    exports components.databases;
    opens components.databases to javafx.fxml;
}
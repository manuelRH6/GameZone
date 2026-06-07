module gamezone {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    opens gamezone to javafx.fxml;
    opens gamezone.ui to javafx.fxml;
    opens gamezone.model to javafx.base;

    exports gamezone;
    exports gamezone.model;
    exports gamezone.interfaces;
    exports gamezone.repository;
    exports gamezone.service;
    exports gamezone.ui;
}

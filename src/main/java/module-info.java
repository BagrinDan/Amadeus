module org.example.amadeus {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.slf4j;
    requires static lombok;

    exports org.example.amadeus.config;
    opens org.example.amadeus.config to com.fasterxml.jackson.databind;

    opens org.example.amadeus to javafx.fxml;
    opens org.example.amadeus.controller to javafx.fxml;

    exports org.example.amadeus;
}
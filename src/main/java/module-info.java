module org.example.amadeus {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.slf4j;

    opens org.example.amadeus to javafx.fxml;
    opens org.example.amadeus.controller to javafx.fxml;

    exports org.example.amadeus;
}
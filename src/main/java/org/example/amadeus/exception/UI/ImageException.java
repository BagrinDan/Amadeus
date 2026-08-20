package org.example.amadeus.exception.UI;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

public class ImageException {
    public static Node createImageMissingPlaceholder() {
        Label errorLabel = new Label("Image\nis\nMissing");
        errorLabel.setStyle(
                "-fx-font-family: 'Monospaced', 'Consolas';" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ff3333;" +
                        "-fx-opacity: 0.7;" +
                        "-fx-border-color: #ff3333; -fx-border-width: 1px; -fx-padding: 20px;"
        );
        errorLabel.setWrapText(true);
        errorLabel.setTextAlignment(TextAlignment.CENTER);
        StackPane.setAlignment(errorLabel, Pos.CENTER);
        return errorLabel;
    }
}

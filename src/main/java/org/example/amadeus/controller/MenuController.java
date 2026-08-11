package org.example.amadeus.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.example.amadeus.service.interfaces.MenuService;

public class MenuController {

    @FXML private TextField userInputField;
    @FXML private VBox chatHistoryBox;
    @FXML private ImageView characterSprite;
    @FXML private StackPane characterImageContainer;
    private final MenuService menuService;

    public MenuController(MenuService menuService){
        this.menuService = menuService;
    }


    @FXML
    public void initialize() {
        if (!tryLoadSprite()) {
            showErrorMessageInImageArea();
        }
    }

    @FXML
    private void handleSendAction() {
        String text = userInputField.getText().trim();
        if (text.isEmpty()) return;

        // 1. Отображаем сообщение пользователя в UI
        Label userLog = new Label("[User] : " + text);
        userLog.getStyleClass().add("user-msg");
        chatHistoryBox.getChildren().add(userLog);

        userInputField.clear();

        // 2. Вызываем сервис для получения ответа
        String response = menuService.processUserInput(text);

        // 3. Отображаем ответ ИИ в UI
        Label aiLog = new Label("[AMADEUS] : " + response);
        aiLog.getStyleClass().add("ai-msg");
        chatHistoryBox.getChildren().add(aiLog);
    }


    private void showErrorMessageInImageArea() {
        characterImageContainer.getChildren().clear();

        Label errorLabel = new Label("Character\nSprite\nMissing");

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

        characterImageContainer.getChildren().add(errorLabel);
    }

    private boolean tryLoadSprite() {
        try {
            var stream = getClass().getResourceAsStream("/org/example/amadeus/sprites/DEFAULT/DEFAULT_1/idle.png");
            if (stream == null) {
                return false;
            }
            Image sprite = new Image(stream);
            characterSprite.setImage(sprite);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

package org.example.amadeus.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.amadeus.config.AppConfig;
import org.example.amadeus.config.UiConfig;
import org.example.amadeus.enums.Emotions;
import org.example.amadeus.exception.UI.ImageException;
import org.example.amadeus.service.SpriteService;
import org.example.amadeus.service.interfaces.MenuService;

import java.util.Random;



public class MenuController {

    @FXML private TextField userInputField;
    @FXML private VBox chatHistoryBox;
    @FXML private ImageView characterSprite;
    @FXML private StackPane characterImageContainer;
    @FXML private SplitPane mainSplitPane;

    private MenuService menuService;
    private final SpriteService spriteService;
    private Timeline talkTimeline;

    private Stage stage;
    private AppConfig appConfig;
    private UiConfig ui;

    public MenuController() {
        this.spriteService = new SpriteService();
    }

    @FXML
    public void initialize() {
        mainSplitPane.setDividerPositions(0.5);

        characterSprite.fitWidthProperty().bind(characterImageContainer.widthProperty());
        characterSprite.setPreserveRatio(true);

        if (!tryLoadSprite()) {
            showErrorMessageInImageArea();
        }
    }

    public void initData(AppConfig appConfig,
                         Stage stage,
                         MenuService menuService) {

        this.appConfig = appConfig;
        this.stage = stage;
        this.ui = appConfig.getUi();
        this.menuService = menuService;

        restoreConfigSettings(this.ui);
        setupConfigListeners(this.ui);
    }


    @FXML
    private void handleSendAction() {
        String text = userInputField.getText().trim();
        if (text.isEmpty()) return;

        Label userLog = new Label("[User] : " + text);
        userLog.getStyleClass().add("user-msg");
        chatHistoryBox.getChildren().add(userLog);

        userInputField.clear();
        userInputField.setDisable(true);

        menuService.sendPromptAsync(text)
                .thenAccept(llmResponse -> Platform.runLater(() -> {
                    userInputField.setDisable(false);
                    userInputField.requestFocus();

                    Emotions emotion = Emotions.fromString(llmResponse.emotion());
                    SpriteService.CharacterFrames frames = spriteService.getFramesForEmotion(emotion);

                    displayResponseWithTypewriter(llmResponse.text(), frames);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        Label errorLog = new Label("[SYSTEM] : Ошибка получения ответа от Amadeus.");
                        errorLog.getStyleClass().add("error-msg");
                        errorLog.setWrapText(true);
                        chatHistoryBox.getChildren().add(errorLog);

                        userInputField.setDisable(false);
                        userInputField.requestFocus();
                    });
                    return null;
                });
    }

    private void displayResponseWithTypewriter(String text, SpriteService.CharacterFrames frames) {
        Label aiLog = new Label("[AMADEUS] : ");
        aiLog.getStyleClass().add("ai-msg");
        aiLog.setWrapText(true);
        aiLog.maxWidthProperty().bind(chatHistoryBox.widthProperty().subtract(20));

        chatHistoryBox.getChildren().add(aiLog);

        startTalkingAnimation(frames);

        final int[] index = {0};

        Timeline typewriterTimeline = new Timeline(new KeyFrame(Duration.millis(25), event -> {
            if (index[0] < text.length()) {
                aiLog.setText("[AMADEUS] : " + text.substring(0, index[0] + 1));
                index[0]++;
            }
        }));

        typewriterTimeline.setCycleCount(text.length());

        typewriterTimeline.setOnFinished(event -> stopTalkingAnimation(frames));

        typewriterTimeline.play();
    }


    private void startTalkingAnimation(SpriteService.CharacterFrames frames) {
        stopTalkingAnimation(null);

        Random random = new Random();
        talkTimeline = new Timeline(new KeyFrame(Duration.millis(120), event -> {
            int frameIndex = random.nextInt(3);
            switch (frameIndex) {
                case 0 -> characterSprite.setImage(frames.idle());
                case 1 -> characterSprite.setImage(frames.talk1());
                case 2 -> characterSprite.setImage(frames.talk2());
            }
        }));

        talkTimeline.setCycleCount(Timeline.INDEFINITE);
        talkTimeline.play();
    }

    private void stopTalkingAnimation(SpriteService.CharacterFrames frames) {
        if (talkTimeline != null) {
            talkTimeline.stop();
        }
        if (frames != null && frames.idle() != null) {
            characterSprite.setImage(frames.idle());
        }
    }

    private void restoreConfigSettings(UiConfig ui){
        if (!mainSplitPane.getDividers().isEmpty()) {
            mainSplitPane.setDividerPositions(ui.getSplitDividerPosition());
        }

        stage.setWidth(ui.getWindowWidth());
        stage.setHeight(ui.getWindowHeight());
        stage.setMaximized(ui.isMaximized());
    }

    private void setupConfigListeners(UiConfig ui){
        if (!mainSplitPane.getDividers().isEmpty()) {
            mainSplitPane.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) -> {
                ui.setSplitDividerPosition(newVal.doubleValue());
            });
        }

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.isMaximized()) ui.setWindowWidth(newVal.doubleValue());
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!stage.isMaximized()) ui.setWindowHeight(newVal.doubleValue());
        });

        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            ui.setMaximized(newVal);
        });
    }

    private void showErrorMessageInImageArea() {
        characterImageContainer.getChildren().setAll(ImageException.createImageMissingPlaceholder());
    }

    private boolean tryLoadSprite() {
        try {
            var stream = getClass().getResourceAsStream("/sprites/DEFAULT/DEFAULT_1/default.png"); // TODO: To check if path is valid after deploy
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

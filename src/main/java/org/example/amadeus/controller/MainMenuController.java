package org.example.amadeus.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.amadeus.config.AppConfig;
import org.example.amadeus.config.UiConfig;
import org.example.amadeus.dto.response.InteractionResult;
import org.example.amadeus.exception.UI.ImageException;
import org.example.amadeus.service.interfaces.AmadeusService;
import org.example.amadeus.utils.CharacterFrames;

import java.util.Random;

public class MainMenuController {

    @FXML private TextField userInputField;
    @FXML private VBox chatHistoryBox;
    @FXML private ImageView characterSprite;
    @FXML private StackPane characterImageContainer;
    @FXML private SplitPane mainSplitPane;

    private AmadeusService amadeusFacade;
    private Timeline talkTimeline;

    private Stage stage;
    private AppConfig appConfig;
    private UiConfig ui;

    @FXML
    public void initialize() {
        mainSplitPane.setDividerPositions(0.5);

        characterSprite.fitWidthProperty().bind(characterImageContainer.widthProperty());
        characterSprite.setPreserveRatio(true);
    }

    public void initData(AppConfig appConfig,
                         Stage stage,
                         AmadeusService amadeusFacade) {

        this.appConfig = appConfig;
        this.stage = stage;
        this.ui = appConfig.getUi();
        this.amadeusFacade = amadeusFacade;

        restoreConfigSettings(this.ui);
        setupConfigListeners(this.ui);

        loadInitialSprite();
    }

    @FXML
    private void handleSendAction() {
        String text = userInputField.getText().trim();
        if (text.isEmpty()) return;

        appendUserLog(text);
        userInputField.clear();
        userInputField.setDisable(true);

        amadeusFacade.processUserPrompt(text)
                .thenAccept(response -> Platform.runLater(() -> {
                    userInputField.setDisable(false);
                    userInputField.requestFocus();

                    displayResponseWithTypewriter(response);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(this::handleSystemError);
                    return null;
                });
    }

    private void loadInitialSprite() {
        try {
            CharacterFrames defaultFrames = amadeusFacade.getDefaultFrames();
            if (defaultFrames != null && defaultFrames.idle() != null) {
                characterSprite.setImage(defaultFrames.idle());
            } else {
                showErrorMessageInImageArea();
            }
        } catch (Exception e) {
            showErrorMessageInImageArea();
        }
    }

    private void appendUserLog(String text) {
        Label userLog = new Label("[User] : " + text);
        userLog.getStyleClass().add("user-msg");
        chatHistoryBox.getChildren().add(userLog);
    }

    private void handleSystemError() {
        Label errorLog = new Label("[SYSTEM] : Unknown error.");
        errorLog.getStyleClass().add("sys-msg");
        errorLog.setWrapText(true);
        chatHistoryBox.getChildren().add(errorLog);

        userInputField.setDisable(false);
        userInputField.requestFocus();
    }

    private void displayResponseWithTypewriter(InteractionResult result) {
        String text = result.text();
        CharacterFrames frames = result.frames();

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

    private void startTalkingAnimation(CharacterFrames frames) {
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

    private void stopTalkingAnimation(CharacterFrames frames) {
        if (talkTimeline != null) {
            talkTimeline.stop();
        }
        if (frames != null && frames.idle() != null) {
            characterSprite.setImage(frames.idle());
        }
    }

    private void showErrorMessageInImageArea() {
        characterImageContainer.getChildren().setAll(ImageException.createImageMissingPlaceholder());
    }

    private void restoreConfigSettings(UiConfig ui) {
        if (!mainSplitPane.getDividers().isEmpty()) {
            mainSplitPane.setDividerPositions(ui.getSplitDividerPosition());
        }

        stage.setWidth(ui.getWindowWidth());
        stage.setHeight(ui.getWindowHeight());
        stage.setMaximized(ui.isMaximized());
    }

    private void setupConfigListeners(UiConfig ui) {
        if (!mainSplitPane.getDividers().isEmpty()) {
            mainSplitPane.getDividers().getFirst().positionProperty().addListener((obs, oldVal, newVal) -> {
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
}
package org.example.amadeus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.amadeus.config.AppConfig;
import org.example.amadeus.config.SettingsManager;
import org.example.amadeus.controller.MainMenuController;
import org.example.amadeus.service.*;
import org.example.amadeus.service.interfaces.AmadeusService;
import org.example.amadeus.service.interfaces.EmbeddedLlmService;
import org.example.amadeus.service.interfaces.LlmService;
import org.example.amadeus.service.interfaces.SpriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private EmbeddedLlmService embeddedLlmService;
    private SettingsManager settingsManager;
    private AppConfig config;

    private LlmService llmService;
    private AmadeusService amadeusFacade;
    private SpriteService spriteService;

    @Override
    public void start(Stage stage) throws IOException {
        final int port = 4701;

        // Uploading config from ~/.config/amadeus/config.json
        this.settingsManager = new SettingsManager();
        this.config = settingsManager.getConfig();

        // Init services
        this.embeddedLlmService = new EmbeddedLlmServiceImpl(port);
        this.llmService = new LlmServiceImpl(port);
        this.spriteService = new SpriteServiceImpl();
        this.amadeusFacade = new AmadeusServiceImpl(llmService, spriteService);

        // Uploading demon to run LLM service
        Thread llmThread = new Thread(() -> {
            try {
                String modelPath = "models/qwen2.5-0.5b-instruct-q4_k_m.gguf";
                embeddedLlmService.startDemon(modelPath);
            } catch (Exception e) {
                log.error("[ERROR | Main] Error at starting LLM demon", e);
            }
        }, "llm-demon-init-thread");
        llmThread.setDaemon(true);
        llmThread.start();

        // Upload FXML interface
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menu/main_menu.fxml"));

        // Injecting dependency
        fxmlLoader.setControllerFactory(clazz -> {
            if (clazz == MainMenuController.class) {
                return new MainMenuController();
            }
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("[EXCEPTION ERROR | MAIN] Cannot create controller: " + clazz.getName(), e);
            }
        });

        // Init scene
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Amadeus ver.0.0.2");
        stage.setScene(scene);

        // Init config date
        MainMenuController controller = fxmlLoader.getController();
        controller.initData(config, stage, amadeusFacade);

        // Saving config data and closing program
        stage.setOnCloseRequest(event -> {
            log.info("[INFO | Main] Closing Amadeus...");
            settingsManager.save();
            if (embeddedLlmService != null) {
                embeddedLlmService.stopDemon();
            }
        });

        // Showing stage
        stage.show();
    }
}

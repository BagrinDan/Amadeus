package org.example.amadeus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.amadeus.controller.MenuController;
import org.example.amadeus.service.EmbeddedLlmServiceImpl;
import org.example.amadeus.service.MenuServiceImpl;
import org.example.amadeus.service.interfaces.EmbeddedLlmService;
import org.example.amadeus.service.interfaces.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage stage) throws IOException {
        // Services init
        final MenuService menuService = new MenuServiceImpl();
        final EmbeddedLlmService embeddedLlmService = new EmbeddedLlmServiceImpl(8080);

        new Thread(() -> {
            try{
                String modelPath = "models/qwen2.5-0.5b-instruct-q4_k_m.gguf";
                embeddedLlmService.startDemon(modelPath);
            } catch (Exception e){
                log.error("[ERROR] Error at starting LLM demon {}", String.valueOf(e));
            }
        }, "llm-demon-init-thread").start();

        // Interface init
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menu/main_menu.fxml"));

        fxmlLoader.setControllerFactory(clazz -> {
            if (clazz == MenuController.class) {
                return new MenuController(menuService);
            }
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Не удалось создать контроллер: " + clazz.getName(), e);
            }
        });

        // Scene init
        Scene scene = new Scene(fxmlLoader.load(), 800, 240);
        stage.setTitle("Amadeus ver.0.0.1");
        stage.setScene(scene);
        stage.show();
    }
}

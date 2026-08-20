package org.example.amadeus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.amadeus.config.AppConfig;
import org.example.amadeus.config.SettingsManager;
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
    private EmbeddedLlmService embeddedLlmService;
    private SettingsManager settingsManager;

    @Override
    public void start(Stage stage) throws IOException {
        final int port = 4701;

        // 1. Загрузка конфигурации (~/.config/amadeus/config.json или дефолтный из resources)
        this.settingsManager = new SettingsManager();
        AppConfig config = settingsManager.getConfig();

        // 2. Инициализация сервисов
        this.embeddedLlmService = new EmbeddedLlmServiceImpl(port);
        final MenuService menuService = new MenuServiceImpl(port);

        // 3. Запуск демона LLM в фоновом потоке
        new Thread(() -> {
            try {
                String modelPath = "models/qwen2.5-0.5b-instruct-q4_k_m.gguf";
                embeddedLlmService.startDemon(modelPath);
            } catch (Exception e) {
                log.error("[ERROR] Error at starting LLM demon", e);
            }
        }, "llm-demon-init-thread").start();

        // 4. Загрузка FXML интерфейса
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menu/main_menu.fxml"));

        // Передаем зависимости в контроллер через фабрику контроллеров
        fxmlLoader.setControllerFactory(clazz -> {
            if (clazz == MenuController.class) {
                return new MenuController();
            }
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Не удалось создать контроллер: " + clazz.getName(), e);
            }
        });

        // 5. Отрисовка сцены
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Amadeus ver.0.0.1");
        stage.setScene(scene);

        // 6. Инициализация данных и подвязка конфигов к контроллеру
        MenuController controller = fxmlLoader.getController();
        controller.initData(config, stage, menuService);

        // 7. Сохранение настроек и остановка сервисов при закрытии окна
        stage.setOnCloseRequest(event -> {
            log.info("Завершение работы Amadeus, сохранение конфигурации...");
            settingsManager.save();
            if (embeddedLlmService != null) {
                embeddedLlmService.stopDemon();
            }
        });

        // Показываем окно
        stage.show();
    }
}

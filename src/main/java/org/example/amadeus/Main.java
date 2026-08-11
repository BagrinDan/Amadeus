package org.example.amadeus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.amadeus.controller.MenuController;
import org.example.amadeus.service.MenuServiceImpl;
import org.example.amadeus.service.interfaces.MenuService;

import java.io.IOException;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MenuService menuService = new MenuServiceImpl();
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

        Scene scene = new Scene(fxmlLoader.load(), 800, 240);
        stage.setTitle("Amadeus ver.0.0.1");
        stage.setScene(scene);
        stage.show();
    }
}

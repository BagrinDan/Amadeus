package org.example.amadeus.service;


import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.amadeus.service.interfaces.MenuService;

public class MenuServiceImpl implements MenuService {

    private TextField userInputField;
    private VBox chatHistoryBox;

    @Override
    public String processUserInput(String prompt) {
        return "Ответ на: " + prompt;
    }
}

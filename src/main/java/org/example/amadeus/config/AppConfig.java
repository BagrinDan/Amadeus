package org.example.amadeus.config;


import lombok.NoArgsConstructor;


@NoArgsConstructor
public class AppConfig {
    private final UiConfig ui = new UiConfig();

    public UiConfig getUi() {
        return ui;
    }
}

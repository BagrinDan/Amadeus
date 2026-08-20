package org.example.amadeus.config;


import lombok.NoArgsConstructor;


@NoArgsConstructor
public class AppConfig {
    private UiConfig ui = new UiConfig();

    public UiConfig getUi() {
        return ui;
    }

    public void setUi(UiConfig ui) {
        this.ui = ui;
    }
}

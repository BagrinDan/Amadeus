package org.example.amadeus.config;

import lombok.NoArgsConstructor;



@NoArgsConstructor
public class UiConfig {
    private double windowWidth = 1000.0;

    private double windowHeight = 600.0;
    private boolean isMaximized = false;

    private double splitDividerPosition = 0.5;

    private String activeSkin = "default";
    private boolean autoScrollChat = true;


    public boolean isAutoScrollChat() {
        return autoScrollChat;
    }

    public void setAutoScrollChat(boolean autoScrollChat) {
        this.autoScrollChat = autoScrollChat;
    }

    public String getActiveSkin() {
        return activeSkin;
    }

    public void setActiveSkin(String activeSkin) {
        this.activeSkin = activeSkin;
    }

    public double getSplitDividerPosition() {
        return splitDividerPosition;
    }

    public void setSplitDividerPosition(double splitDividerPosition) {
        this.splitDividerPosition = splitDividerPosition;
    }

    public boolean isMaximized() {
        return isMaximized;
    }

    public void setMaximized(boolean maximized) {
        isMaximized = maximized;
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(double windowHeight) {
        this.windowHeight = windowHeight;
    }

    public double getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(double windowWidth) {
        this.windowWidth = windowWidth;
    }
}

package org.example.amadeus.utils;


import javafx.scene.image.Image;

public record CharacterFrames(
        Image idle,
        Image talk1,
        Image talk2
) {
    public boolean isValid() {
        return idle != null;
    }
}

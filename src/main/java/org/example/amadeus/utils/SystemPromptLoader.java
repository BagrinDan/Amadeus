package org.example.amadeus.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class SystemPromptLoader {
    public static String loadPromptResource(String resourcePath) {
        try (InputStream inputStream = SystemPromptLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return null;
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("[ERROR] Can't get system prompt from file: " + resourcePath, e);
        }
    }
}

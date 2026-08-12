package org.example.amadeus.service.interfaces;

public interface EmbeddedLlmService {
    void startDemon(String modelPath);
    void stopDemon();
    boolean isDemonRunning();
}

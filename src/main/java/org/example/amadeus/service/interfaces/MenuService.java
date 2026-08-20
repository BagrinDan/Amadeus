package org.example.amadeus.service.interfaces;

import org.example.amadeus.dto.response.LlmResponse;

import java.util.concurrent.CompletableFuture;

public interface MenuService {
    CompletableFuture<LlmResponse> sendPromptAsync(String userPrompt);
}

package org.example.amadeus.service.interfaces;

import org.example.amadeus.dto.response.InteractionResult;

import java.util.concurrent.CompletableFuture;


public interface AmadeusService extends AmadeusCRUD{
    CompletableFuture<InteractionResult> processUserPrompt(String text);
}

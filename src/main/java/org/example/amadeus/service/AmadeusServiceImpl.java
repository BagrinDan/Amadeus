package org.example.amadeus.service;


import org.example.amadeus.dto.response.InteractionResult;
import org.example.amadeus.enums.Emotions;
import org.example.amadeus.service.interfaces.AmadeusService;
import org.example.amadeus.service.interfaces.LlmService;
import org.example.amadeus.service.interfaces.SpriteService;
import org.example.amadeus.utils.CharacterFrames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

// Facade class
public class AmadeusServiceImpl implements AmadeusService {
    private static final Logger log = LoggerFactory.getLogger(AmadeusServiceImpl.class);

    private final LlmService llmService;
    private final SpriteService spriteService;

    public AmadeusServiceImpl(LlmService llmService,
                              SpriteService spriteService) {
        this.llmService = llmService;
        this.spriteService = spriteService;
    }

    @Override
    public CompletableFuture<InteractionResult> processUserPrompt(String text) {
        log.debug("[DEBUG | AmadeusService: Text {}]", text);

        return llmService.sendPromptAsync(text)
                .thenApply(llmResponse -> {
                    Emotions emotion = Emotions.fromString(llmResponse.emotion());
                    log.debug("[DEBUG | AmadeusService: Emotion {}]", emotion.toString());

                    CharacterFrames frames = spriteService.getFramesForEmotion(emotion);
                    log.debug("[DEBUG | AmadeusService: Frames {}]", frames.toString());

                    return new InteractionResult(llmResponse.text(), frames);
                });
    }

    @Override
    public CharacterFrames getDefaultFrames() {
        return spriteService.getFramesForEmotion(Emotions.DEFAULT);
    }
}

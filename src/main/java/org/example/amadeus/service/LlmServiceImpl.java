package org.example.amadeus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.amadeus.dto.response.LlmResponse;
import org.example.amadeus.service.interfaces.LlmService;
import org.example.amadeus.utils.SystemPromptLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class LlmServiceImpl implements LlmService {
    private static final Logger log = LoggerFactory.getLogger(LlmServiceImpl.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String serverUrl;

    public LlmServiceImpl(int port) {
        this.serverUrl = "http://127.0.0.1:" + port + "/v1/chat/completions";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<LlmResponse> sendPromptAsync(String userPrompt){
        String systemPrompt = SystemPromptLoader.loadPromptResource("/Kurisu.md");

        if(systemPrompt == null){
            systemPrompt = "You're Amadeus from Steins;Gate. Act like Amadeus!";
            log.info("[WARN : LlmService] System prompt file not found. Using default short string");
        }

        try{
            log.debug("[DEBUG : LlmService] Building raw request body...");
            Map<String, Object> requestBody = Map.of(
                    "messages", new Object[]{
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt),
                    },
                    "temperature", 0.2,
                    "max_tokens", 256
            );

            String jsonPayload = objectMapper.writeValueAsString(requestBody);

            log.debug("[DEBUG : LLM Client] Building http request...");
            HttpRequest request= HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            log.debug("[DEBUG : LLM Client] Sending request to LLM server...");
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            LlmResponse llmResponse = parseLlmResponse(response.body());

                            log.debug("[DEBUG : LLM Client] Selected Emotion: {}, Response Text: {}",
                                    llmResponse.emotion(),
                                    llmResponse.text());

                            return llmResponse;
                        } else {
                            throw new RuntimeException("[ERROR : LLM Client] HTTP Error: " + response.statusCode());
                        }
                    });

            } catch (Exception e) {
                log.error("[ERROR : LlmService] Failed to build or send request", e);
                return CompletableFuture.failedFuture(e);
            }
        }


    private LlmResponse parseLlmResponse(String jsonResponseBody) {
        log.debug("[DEBUG | LLM Client]: Llm Answer: {}", jsonResponseBody);

        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponseBody);
            String rawContent = rootNode
                    .path("choices").get(0)
                    .path("message").path("content").asText().trim();

            if (rawContent.startsWith("```")) {
                rawContent = rawContent.replaceAll("^```[a-z]*\\n?", "").replaceAll("\\n?```$", "").trim();
            }

            try {
                JsonNode responseJson = objectMapper.readTree(rawContent);

                if (responseJson.isObject()) {
                    String emotion = responseJson.path("emotion").asText("DEFAULT");
                    String text = responseJson.path("text").asText(rawContent);
                    return new LlmResponse(emotion, text);
                }
            } catch (Exception ignored) { }

            return new LlmResponse("DEFAULT", rawContent);

        } catch (Exception e) {
            log.error("[ ERROR | LlmService ] Failed to parse JSON response", e);
            return new LlmResponse("DEFAULT", "Не удалось обработать ответ.");
        }
    }
}


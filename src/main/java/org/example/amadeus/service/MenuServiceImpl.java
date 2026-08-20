package org.example.amadeus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.amadeus.dto.response.LlmResponse;
import org.example.amadeus.service.interfaces.MenuService;
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


public class MenuServiceImpl implements MenuService {
    private static final Logger log = LoggerFactory.getLogger(MenuServiceImpl.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String serverUrl;

    public MenuServiceImpl(int port) {
        this.serverUrl = "http://127.0.0.1:" + port + "/v1/chat/completions";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<LlmResponse> sendPromptAsync(String userPrompt){
        String systemPrompt = SystemPromptLoader.loadPromptResource("/Kurisu.md");

        if(systemPrompt == null){
            systemPrompt = "You're Amadeus! Act like Amadeus!";
        }

        log.info("[DEBUG: LLM Client] System prompt length: {}", systemPrompt.length());

        try{
            log.info("[DEBUG: LLM Client] Building raw request body...");
            Map<String, Object> requestBody = Map.of(
                    "messages", new Object[]{
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt),
                    },
                    "temperature", 0.7,
                    "max_tokens", 256
            );

            String jsonPayload = objectMapper.writeValueAsString(requestBody);

            log.info("[DEBUG: LLM Client] Building http request...");
            HttpRequest request= HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            log.info("[DEBUG: LLM Client] Sending request to LLM server...");

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            LlmResponse llmResponse = parseLlmResponse(response.body());

                            log.info("[DEBUG: MenuService] Selected Emotion: {}, Response Text: {}",
                                    llmResponse.emotion(),
                                    llmResponse.text());

                            // 3. Возвращаем распарсенный объект дальше
                            return llmResponse;
                        } else {
                            throw new RuntimeException("HTTP Error: " + response.statusCode());
                        }
                    });

            } catch (Exception e) {
                log.error("[DEBUG: LLM Client] Failed to build or send request", e);
                return CompletableFuture.failedFuture(e);
            }
        }


    private LlmResponse parseLlmResponse(String jsonResponseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponseBody);
            String rawContent = rootNode
                    .path("choices").get(0)
                    .path("message").path("content").asText().trim();

            if (rawContent.startsWith("```")) {
                rawContent = rawContent.replaceAll("^```[a-z]*\\n?", "").replaceAll("\\n?```$", "").trim();
            }

            JsonNode responseJson = objectMapper.readTree(rawContent);
            String emotion = responseJson.path("emotion").asText("DEFAULT");
            String text = responseJson.path("text").asText(rawContent);

            return new LlmResponse(emotion, text);
        } catch (Exception e) {
            log.error("Failed to parse JSON response", e);
            return new LlmResponse("DEFAULT", "Не удалось обработать ответ.");
        }
    }
}


package org.app.aicontentservice.client;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    public String generate(String prompt) {
        // The try-with-resources is correct as the Client is AutoCloseable
        try (Client client = Client.builder()
                .apiKey(apiKey)
                .build()) {

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3-flash-preview", // Updated Model ID
                            prompt,
                            null);

            return response.text();
        } catch (Exception e) {
            // It is good practice to handle potential API errors
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}
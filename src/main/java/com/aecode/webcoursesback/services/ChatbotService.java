package com.aecode.webcoursesback.services;
/*
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Service
public class ChatbotService {

    private final WebClient webClient;

    // Inyectamos la variable de entorno OPENAI_API_KEY directamente
    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    public ChatbotService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }

    // Eliminamos el método @PostConstruct, ya que la variable de entorno se inyecta
    // directamente
    public Mono<String> getResponse(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("API Key no configurada o vacía.");
            throw new IllegalStateException("API Key de OpenAI no configurada correctamente.");
        }

        String body = "{"
                + "\"model\": \"gpt-4\","
                + "\"messages\": ["
                + "{\"role\": \"user\", \"content\": \"" + prompt + "\"} "
                + "]"
                + "}";

        return this.webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    // Verifica si la respuesta es un JSON válido
                    try {
                        // Parsear la respuesta para asegurar que es válida
                        new JSONObject(response);
                        return response;
                    } catch (Exception e) {
                        return "Error en la respuesta del servidor: " + e.getMessage();
                    }
                });
    }

}
*/
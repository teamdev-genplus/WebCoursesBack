package com.aecode.webcoursesback.services;

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
                    // Procesar la respuesta para extraer solo el contenido del mensaje
                    String message = extractMessage(response);
                    return message; // Retornar solo el mensaje del asistente
                });
    }

    private String extractMessage(String response) {
        try {
            // Parsear el JSON de la respuesta
            JSONObject jsonResponse = new JSONObject(response);
            // Extraer el contenido del mensaje
            String message = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar la respuesta de OpenAI.";
        }
    }
}

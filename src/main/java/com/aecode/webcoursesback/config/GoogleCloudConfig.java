package com.aecode.webcoursesback.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class GoogleCloudConfig {
    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudConfig.class);

    @Value("${GOOGLE_CREDENTIALS}")
    private String googleCredentialsJson;

    @Bean
    public SecretManagerServiceClient secretManagerServiceClient() throws IOException {
        try {
            // Validar y formatear el JSON
            String formattedJson = validateAndFormatJson(googleCredentialsJson);

            GoogleCredentials credentials;
            try (ByteArrayInputStream credentialsStream = new ByteArrayInputStream(formattedJson.getBytes())) {
                credentials = GoogleCredentials.fromStream(credentialsStream);
            }

            SecretManagerServiceSettings settings = SecretManagerServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            return SecretManagerServiceClient.create(settings);
        } catch (Exception e) {
            logger.error("Error al crear el cliente de Secret Manager. Contenido de GOOGLE_CREDENTIALS: {}",
                    googleCredentialsJson.substring(0, Math.min(googleCredentialsJson.length(), 100)) + "...");
            throw new RuntimeException("Error al inicializar Secret Manager Client", e);
        }
    }

    private String validateAndFormatJson(String jsonString) {
        try {
            // Eliminar posibles caracteres especiales o espacios al inicio y final
            jsonString = jsonString.trim();

            // Si el JSON está envuelto en comillas simples o dobles, las removemos
            if ((jsonString.startsWith("'") && jsonString.endsWith("'")) ||
                    (jsonString.startsWith("\"") && jsonString.endsWith("\""))) {
                jsonString = jsonString.substring(1, jsonString.length() - 1);
            }

            // Validar que sea un JSON válido
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

            // Convertir de vuelta a string formateado
            return gson.toJson(jsonObject);
        } catch (Exception e) {
            logger.error("Error al validar el JSON de credenciales", e);
            throw new RuntimeException("El formato de las credenciales no es válido", e);
        }
    }
}
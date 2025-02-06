package com.aecode.webcoursesback.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class GoogleCloudConfig {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudConfig.class);

    @PostConstruct
    public void init() {
        // Este método se ejecutará después de la inyección de dependencias
        String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        // Este método se ejecutará después de la inyección de dependencias
        logger.info("Iniciando configuración de Google Cloud");
        logger.info("Ruta de credenciales configurada: {}", credentialsPath);

        // Verificación de las variables de entorno
        logger.info("Variables de entorno disponibles:");
        logger.info("GOOGLE_APPLICATION_CREDENTIALS: {}", credentialsPath);

        if (credentialsPath == null) {
            logger.error("La variable de entorno GOOGLE_APPLICATION_CREDENTIALS no está configurada");
            throw new RuntimeException("La variable de entorno GOOGLE_APPLICATION_CREDENTIALS no está configurada.");
        }
    }

    @PostConstruct
    public void validateCredentialsFile() {
        try {
            String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            File file = new File(credentialsPath);
            logger.info("Validando archivo de credenciales:");
            logger.info("¿Existe?: {}", file.exists());
            logger.info("¿Se puede leer?: {}", file.canRead());
            logger.info("Tamaño: {} bytes", file.length());

            // Intentar leer el contenido como JSON para validar
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(file);
            logger.info("Archivo JSON válido");
        } catch (Exception e) {
            logger.error("Error validando archivo de credenciales", e);
        }
    }

    @Bean
    public SecretManagerServiceClient secretManagerServiceClient() {
        try {
            logger.info("Intentando crear SecretManagerServiceClient");

            String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            if (credentialsPath == null) {
                throw new RuntimeException("La ruta de credenciales no está configurada.");
            }

            File credentialsFile = new File(credentialsPath);
            if (!credentialsFile.exists()) {
                throw new RuntimeException("Archivo de credenciales no encontrado en: " + credentialsPath);
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new FileInputStream(credentialsFile));

            logger.info("Credenciales cargadas exitosamente");

            SecretManagerServiceSettings settings = SecretManagerServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            SecretManagerServiceClient client = SecretManagerServiceClient.create(settings);
            logger.info("Cliente de Secret Manager creado exitosamente");

            return client;

        } catch (IOException e) {
            String errorMessage = "Error al crear SecretManagerServiceClient: " + e.getMessage();
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = "Error inesperado al crear SecretManagerServiceClient: " + e.getMessage();
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }
}
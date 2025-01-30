package com.aecode.webcoursesback.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;

@Configuration
public class GoogleCloudConfig {
    @Bean
    public SecretManagerServiceClient secretManagerServiceClient() throws IOException {
        GoogleCredentials credentials;
        try (InputStream credentialsStream = new ClassPathResource("google-credentials.json").getInputStream()) {
            credentials = GoogleCredentials.fromStream(credentialsStream);
        }

        SecretManagerServiceSettings settings = SecretManagerServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        return SecretManagerServiceClient.create(settings);
    }
}
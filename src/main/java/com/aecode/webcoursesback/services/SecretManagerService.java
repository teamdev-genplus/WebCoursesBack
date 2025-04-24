package com.aecode.webcoursesback.services;

import com.google.cloud.secretmanager.v1.*;

import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import java.nio.charset.StandardCharsets;

@Service
public class SecretManagerService {

    private String projectId = "digitalproduct-6d2f8";

    public String getSecret(String secretId) {
        // Crea un cliente para Secret Manager
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {

            // Construye el nombre del secreto
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, "latest");

            // Obtén el secreto
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);

            // Lee el contenido del secreto
            ByteString payload = response.getPayload().getData();
            return payload.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

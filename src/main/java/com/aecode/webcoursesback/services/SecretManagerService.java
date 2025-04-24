package com.aecode.webcoursesback.services;

import com.google.cloud.secretmanager.v1.*;

import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import java.nio.charset.StandardCharsets;

@Service
public class SecretManagerService {

    private String projectId = "digitalproduct-6d2f8";

    public String getSecret(String secretId) {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            System.out.println("Obteniendo secreto: " + secretId + " para el proyecto: " + projectId);

            // Construir el nombre del secreto
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, "latest");

            // Obtener el secreto
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);

            // Leer el contenido del secreto
            ByteString payload = response.getPayload().getData();
            String secretValue = payload.toString(StandardCharsets.UTF_8);

            System.out.println("Secreto obtenido: " + secretValue); // Imprimir el valor del secreto

            return secretValue;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al obtener el secreto: " + e.getMessage());
            return null;
        }
    }
}

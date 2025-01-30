package com.aecode.webcoursesback.services;

import org.springframework.stereotype.Service;

import com.google.cloud.secretmanager.v1.*;
import org.springframework.beans.factory.annotation.Value;

@Service
public class SecreteManagerService {
    private final SecretManagerServiceClient secretManagerServiceClient;
    private final String projectId;

    public SecreteManagerService(
            SecretManagerServiceClient secretManagerServiceClient,
            @Value("${google.cloud.project-id}") String projectId) {
        if (secretManagerServiceClient == null) {
            throw new NullPointerException("secretManagerServiceClient cannot be null");
        }
        this.secretManagerServiceClient = secretManagerServiceClient;
        this.projectId = projectId;
    }

    public String getSecret(String secretId, String version) {
        try {
            String secretName = String.format("projects/%s/secrets/%s/versions/%s",
                    projectId,
                    secretId,
                    version);

            AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder()
                    .setName(secretName)
                    .build();

            AccessSecretVersionResponse response = secretManagerServiceClient.accessSecretVersion(request);
            return response.getPayload().getData().toStringUtf8();

        } catch (com.google.api.gax.rpc.NotFoundException e) {
            throw new RuntimeException("El secreto no fue encontrado: " + secretId, e);
        } catch (com.google.api.gax.rpc.PermissionDeniedException e) {
            throw new RuntimeException("No tienes permisos para acceder al secreto: " + secretId, e);
        } catch (Exception e) {
            throw new RuntimeException("Error al acceder al secreto: " + secretId, e);
        }
    }

}

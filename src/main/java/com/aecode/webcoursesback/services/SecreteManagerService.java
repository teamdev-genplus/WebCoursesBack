package com.aecode.webcoursesback.services;

import org.springframework.stereotype.Service;

import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;

@Service
public class SecreteManagerService {
    public String getSecret(String secretId, String version) {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            String secretName = String.format("projects/%s/secrets/%s/versions/%s",
                    "crucial-axon-448818-r9",
                    secretId,
                    version);

            AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder()
                    .setName(secretName)
                    .build();

            AccessSecretVersionResponse response = client.accessSecretVersion(request);

            return response.getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            throw new RuntimeException("Error al acceder al secreto: " + secretId, e);
        }
    }

}

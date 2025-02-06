package com.aecode.webcoursesback.services;

import org.springframework.stereotype.Service;

import com.google.cloud.secretmanager.v1.*;
import com.google.protobuf.ByteString;
import java.io.IOException;

@Service
public class GoogleCloudService {

    private final String secretName = "projects/crucial-axon-448818-r9/secrets/firebase-credentials/versions/latest";

    public String getGoogleCredentials() throws IOException {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName versionName = SecretVersionName.of("crucial-axon-448818-r9", "firebase-credentials",
                    "latest");
            AccessSecretVersionResponse response = client.accessSecretVersion(versionName);
            ByteString payload = response.getPayload().getData();
            return payload.toStringUtf8();
        }
    }
}

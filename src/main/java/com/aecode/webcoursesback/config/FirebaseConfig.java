package com.aecode.webcoursesback.config;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccount =
                getClass().getClassLoader().getResourceAsStream("firebase-config.json");

        if (serviceAccount == null) {
            throw new IOException("Archivo firebase-config.json no encontrado en resources");
        }


//        String firebaseConfigJson = System.getenv("FIREBASE_CONFIG_JSON");
//        if (firebaseConfigJson == null) {
//            throw new IOException("Variable de entorno FIREBASE_CONFIG_JSON no encontrada");
//        }
//        InputStream serviceAccount = new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("digitalproduct-6d2f8.firebasestorage.app")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }
    }
}

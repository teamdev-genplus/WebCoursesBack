package com.aecode.webcoursesback.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageOptions;

@Service
public class ImageUploadingService {

    public String uploadFile(File file, String fileName, String directoryPath) throws IOException {
        InputStream inputStream = ImageUploadingService.class.getClassLoader()
                .getResourceAsStream("digitalproduct-6d2f8-firebase-adminsdk-2f88c-dafb14c702.json");

        if (inputStream == null) {
            throw new IllegalArgumentException("El archivo de credenciales no se encontró en el classpath");
        }

        // Normalizar el directoryPath
        String normalizedPath = directoryPath;
        if (!normalizedPath.endsWith("/")) {
            normalizedPath += "/";
        }
        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }

        String fullPath = normalizedPath + fileName;

        String contentType = determineContentType(fileName);

        BlobId blobId = BlobId.of("digitalproduct-6d2f8.firebasestorage.app", fullPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        com.google.cloud.storage.Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();

        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/digitalproduct-6d2f8.firebasestorage.app/o/%s?alt=media";
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fullPath, StandardCharsets.UTF_8));
    }

    private String determineContentType(String fileName) {
        String extension = fileName.toLowerCase();
        if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (extension.endsWith(".png")) {
            return "image/png";
        } else if (extension.endsWith(".gif")) {
            return "image/gif";
        } else if (extension.endsWith(".webp")) {
            return "image/webp";
        } else if (extension.endsWith(".svg")) {
            return "image/svg";
        }
        return "application/octet-stream";
    }

    public File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }

        return tempFile;
    }

    public String upload(MultipartFile multipartFile, String directoryPath) {
        try {
            String fileName = multipartFile.getOriginalFilename();

            File file = this.convertToFile(multipartFile, fileName);
            String URL = this.uploadFile(file, fileName, directoryPath);
            file.delete();

            return URL;

        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        }
    }

}

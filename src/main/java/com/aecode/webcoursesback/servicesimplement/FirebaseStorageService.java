package com.aecode.webcoursesback.servicesimplement;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;



@Service
public class FirebaseStorageService {

    @Autowired
    private Storage storage;

    @Value("${firebase.storage.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String customFileName) throws IOException {
        // Si no se proporciona un nombre de archivo personalizado, genera uno
        if (customFileName == null || customFileName.isEmpty()) {
            customFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        }

        // Crea la información del blob
        BlobId blobId = BlobId.of(bucketName, customFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        // Sube el archivo y valida que se subió correctamente
        BlobInfo uploadedBlob = storage.create(blobInfo, file.getBytes());

        if (storage.get(bucketName) == null) {
            throw new IOException("El bucket especificado no existe o no es accesible: " + bucketName);
        }


        // Verifica si el archivo subido existe en el bucket
        if (uploadedBlob == null || storage.get(blobId) == null) {
            throw new IOException("La carga del archivo falló. El archivo no existe en Firebase Storage.");
        }


        // Construye y devuelve la URL pública
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, customFileName);
    }

    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, null);
    }

    public void deleteFile(String fileName) {
        storage.delete(bucketName, fileName);
    }
}

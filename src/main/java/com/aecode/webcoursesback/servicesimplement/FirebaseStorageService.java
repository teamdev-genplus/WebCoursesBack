package com.aecode.webcoursesback.servicesimplement;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class FirebaseStorageService {
    private final FirebaseApp firebaseApp;

    @Autowired
    public FirebaseStorageService(FirebaseApp firebaseApp) {
        this.firebaseApp = firebaseApp;
    }

    private Bucket getBucket() {
        return StorageClient.getInstance(firebaseApp).bucket();
    }

    public String uploadImage(MultipartFile file, String path) throws IOException {
        Bucket bucket = getBucket();

        String fileName = path + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        bucket.create(fileName, file.getBytes(), file.getContentType());

        String bucketName = bucket.getName();
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
        String publicUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" + encodedFileName + "?alt=media";

        return publicUrl;
    }

    public void deleteImage(String imageUrl) {
        try {
            Bucket bucket = getBucket();

            String prefix1 = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/";
            String prefix2 = "https://storage.googleapis.com/download/storage/v1/b/" + bucket.getName() + "/o/";

            String objectName = null;

            if (imageUrl.startsWith(prefix1)) {
                String pathWithParams = imageUrl.substring(prefix1.length());
                String pathEncoded = pathWithParams.split("\\?")[0];
                objectName = URLDecoder.decode(pathEncoded, StandardCharsets.UTF_8.name());
            } else if (imageUrl.startsWith(prefix2)) {
                String pathWithParams = imageUrl.substring(prefix2.length());
                String pathEncoded = pathWithParams.split("\\?")[0];
                objectName = URLDecoder.decode(pathEncoded, StandardCharsets.UTF_8.name());
            } else {
                System.err.println("URL no válida para Firebase Storage: " + imageUrl);
                return;
            }

            System.out.println("Objeto a eliminar: " + objectName);

            Blob blob = bucket.get(objectName);
            if (blob != null) {
                boolean deleted = blob.delete();
                if (deleted) {
                    System.out.println("Archivo eliminado correctamente: " + objectName);
                } else {
                    System.err.println("No se pudo eliminar el archivo: " + objectName);
                }
            } else {
                System.err.println("No se encontró el archivo para eliminar: " + objectName);
            }
        } catch (Exception e) {
            System.err.println("Error eliminando imagen en Firebase Storage: " + e.getMessage());
        }
    }
}

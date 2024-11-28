package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserDetailDTO;
import com.aecode.webcoursesback.entities.UserDetail;
import com.aecode.webcoursesback.services.IUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userdetail")
public class UserDetailController {
    @Autowired
    private IUserDetailService udS;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insert(@RequestPart(value="file", required = false) MultipartFile imagen,
                                         @RequestPart(value = "data", required = false) String dtoJson) {
        String originalFilename = null;
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UserDetailDTO dto= objectMapper.readValue(dtoJson, UserDetailDTO.class);

            String userUploadDir = uploadDir + File.separator + "userdetail";
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Manejo del archivo de script
            if (imagen != null && !imagen.isEmpty()) {
                originalFilename = imagen.getOriginalFilename();;
                byte[] bytes = imagen.getBytes();
                Path path = userUploadPath.resolve(originalFilename);
                Files.write(path, bytes);
            }

            // Convertir DTO a entidad
            ModelMapper modelMapper = new ModelMapper();
            UserDetail userDetail = modelMapper.map(dto, UserDetail.class);
            // Establecer la ruta del archivo en la entidad
            userDetail.setProfilepicture("/uploads/userdetail/" +originalFilename);
            udS.insert(userDetail);

            return ResponseEntity.ok("Información del usuario guardado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo de imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar el objeto en la base de datos: " + e.getMessage());
        }
    }


    @GetMapping
    public List<UserDetailDTO> list() {
        return udS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, UserDetailDTO.class);
        }).collect(Collectors.toList());
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){udS.delete(id);}

    @GetMapping("/{id}")
    public UserDetailDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        UserDetailDTO dto=m.map(udS.listId(id),UserDetailDTO.class);
        return dto;
    }
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(@RequestPart(value="file", required = false) MultipartFile document,
                                         @RequestPart(value = "data", required = false) String dtoJson) {
        String originalFilename = null;
        try {
            // Convertir el JSON a SessionDTO
            ObjectMapper objectMapper = new ObjectMapper();
            UserDetailDTO dto = objectMapper.readValue(dtoJson, UserDetailDTO.class);

            // Obtener la clase existente desde la base de datos
            UserDetail existingSession = udS.listId(dto.getDetailsId());
            if (existingSession == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Información del usuario no encontrada");
            }

            String userUploadDir = uploadDir + File.separator + "userdetail";
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Manejo del archivo de documento
            if (document != null && !document.isEmpty()) {
                // Si se sube un nuevo documento, reemplazar el documento actual
                originalFilename = document.getOriginalFilename();
                byte[] bytes = document.getBytes();
                Path path = userUploadPath.resolve(originalFilename);
                Files.write(path, bytes);

                // Actualizar la ruta del documento en la entidad
                existingSession.setProfilepicture("/uploads/userdetail/"+ originalFilename);
            }

            // Actualizar otros datos de la clase
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.map(dto, existingSession);  // Mapear solo los cambios del DTO a la clase existente

            // Guardar los cambios en la base de datos
            udS.insert(existingSession);

            return ResponseEntity.ok("Información del usuario actualizada correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo de documento: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la clase: " + e.getMessage());
        }
    }

}

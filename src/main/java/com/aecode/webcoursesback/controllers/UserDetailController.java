package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.SessionAnswerDTO;
import com.aecode.webcoursesback.dtos.UserDetailDTO;
import com.aecode.webcoursesback.entities.SessionAnswer;
import com.aecode.webcoursesback.entities.UserDetail;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.IUserDetailService;
import com.aecode.webcoursesback.services.IUserProfileService;
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
    @Autowired
    private IUserProfileService upS;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insert(@RequestPart(value = "file", required = false) MultipartFile imagen,
                                         @RequestPart(value = "data", required = false) String dtoJson) {
        String originalFilename = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UserDetailDTO dto = objectMapper.readValue(dtoJson, UserDetailDTO.class);

            // Buscar y asignar UserProfile desde el servicio
            UserProfile userProfile = null;
            if (dto.getUserId() != 0) {
                userProfile = upS.listId(dto.getUserId());
                if (userProfile == null || userProfile.getUserId() == 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado con el ID proporcionado");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de usuario no proporcionado");
            }

            // Crear directorio único para el usuario si no existe
            String userUploadDir = uploadDir + File.separator + "userdetail" + File.separator + userProfile.getUserId();
            Path userUploadPath = Paths.get(userUploadDir);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // Manejo del archivo de imagen
            if (imagen != null && !imagen.isEmpty()) {
                originalFilename = imagen.getOriginalFilename();
                byte[] bytes = imagen.getBytes();
                Path path = userUploadPath.resolve(originalFilename);
                Files.write(path, bytes);
            }

            // Convertir DTO a entidad
            ModelMapper modelMapper = new ModelMapper();
            UserDetail userDetail = modelMapper.map(dto, UserDetail.class);

            // Asignar el UserProfile y la ruta de la imagen
            userDetail.setUserProfile(userProfile);
            userDetail.setProfilepicture("/uploads/userdetail/" + userProfile.getUserId() + "/" + originalFilename);

            // Guardar los detalles del usuario
            udS.insert(userDetail);

            return ResponseEntity.ok("Información del usuario guardada correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo de imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar el objeto en la base de datos: " + e.getMessage());
        }
    }



    @GetMapping
    public List<UserDetailDTO> list() {
        ModelMapper m = new ModelMapper();
        List<UserDetail> u = udS.list();

        return u.stream().map(userdetail -> {
            UserDetailDTO dto = m.map(userdetail, UserDetailDTO.class);

            // Validar si userProfile no es null
            if (userdetail.getUserProfile() != null) {
                dto.setUserId(userdetail.getUserProfile().getUserId());
            } else {
                // Manejar casos donde userProfile es null (opcional)
                dto.setUserId(0); // O asignar un valor por defecto
            }

            return dto;
        }).collect(Collectors.toList());
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){udS.delete(id);}

    @GetMapping("/{id}")
    public UserDetailDTO listById(@PathVariable("id") Integer id) {
        ModelMapper m = new ModelMapper();
        // Obtener el usuario por id
        UserDetail userDetail = udS.listId(id);

        // Mapear el objeto UserDetail a UserDetailDTO
        UserDetailDTO dto = m.map(userDetail, UserDetailDTO.class);

        // Validar si userProfile no es null
        if (userDetail.getUserProfile() != null) {
            dto.setUserId(userDetail.getUserProfile().getUserId());
        } else {
            // Asignar un valor por defecto si userProfile es null
            dto.setUserId(0); // O asignar el valor que desees
        }

        return dto;
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updatePartial(@RequestPart(value = "file", required = false) MultipartFile document,
                                                @RequestPart(value = "data", required = false) String dtoJson) {
        String originalFilename = null;
        try {
            // Convertir el JSON a UserDetailDTO
            ObjectMapper objectMapper = new ObjectMapper();
            UserDetailDTO dto = objectMapper.readValue(dtoJson, UserDetailDTO.class);

            // Obtener la clase existente desde la base de datos
            UserDetail existingUserDetail = udS.listId(dto.getDetailsId());
            if (existingUserDetail == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Información del usuario no encontrada");
            }

            // Manejo del archivo de documento (si es que se ha subido uno)
            if (document != null && !document.isEmpty()) {
                // Si se sube un nuevo documento, reemplazar el documento actual
                originalFilename = document.getOriginalFilename();
                byte[] bytes = document.getBytes();
                Path path = Paths.get(uploadDir + File.separator + "userdetail").resolve(originalFilename);
                Files.write(path, bytes);

                // Actualizar la ruta del documento en la entidad
                existingUserDetail.setProfilepicture("/uploads/userdetail/" + originalFilename);
            }

            // Actualizar parcialmente los campos del UserDetail (si están presentes en el DTO)
            if (dto.getUserId() != 0) {
                // Buscar el UserProfile y asignarlo
                UserProfile userProfile = upS.listId(dto.getUserId());
                if (userProfile == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado con el ID proporcionado");
                }
                existingUserDetail.setUserProfile(userProfile);
            }

            // Actualizar otros campos de la entidad si se proporcionan en el DTO
            if (dto.getProfilepicture() != null) {
                existingUserDetail.setProfilepicture(dto.getProfilepicture());
            }

            // Guardar los cambios en la base de datos
            udS.insert(existingUserDetail);

            return ResponseEntity.ok("Información del usuario actualizada correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo de documento: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la clase: " + e.getMessage());
        }
    }


}

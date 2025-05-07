package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserDetailDTO;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.services.IUserDetailService;
import com.aecode.webcoursesback.services.IUserProfileService;
import com.aecode.webcoursesback.servicesimplement.FirebaseStorageService;
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
    @Autowired
    private FirebaseStorageService firebaseStorageService;

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

            String imageUrl = null;
            if (imagen != null && !imagen.isEmpty()) {
                imageUrl = firebaseStorageService.uploadImage(imagen,dto.getUserId());
            }

            // Convertir DTO a entidad
            ModelMapper modelMapper = new ModelMapper();
            UserDetail userDetail = modelMapper.map(dto, UserDetail.class);

            // Asignar el UserProfile y la ruta de la imagen
            userDetail.setUserProfile(userProfile);
            userDetail.setProfilepicture(imageUrl);

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

    @GetMapping("/by-user/{userId}") // Nuevo endpoint para buscar por userId
    public UserDetailDTO findByUserId(@PathVariable int userId) {
        UserDetail userDetail = udS.findByUserId(userId);
        if (userDetail == null) {
            return null;
        }
        UserDetailDTO dto = new UserDetailDTO();
        dto.setDetailsId(userDetail.getDetailsId());
        dto.setUserId(userDetail.getUserProfile().getUserId());
        dto.setProfilepicture(userDetail.getProfilepicture());
        return dto;
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(
            @PathVariable("id") Integer id,
            @RequestPart(value = "file", required = false) MultipartFile picture,
            @RequestPart(value = "data", required = false) String dtoJson) {

        try {
            // Obtener el UserDetail existente por ID
            UserDetail existingUDetail = udS.listId(id);
            if (existingUDetail == null || existingUDetail.getDetailsId() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Información del usuario no encontrada");
            }

            UserProfile userProfile = existingUDetail.getUserProfile();

            // Procesar datos enviados en JSON para actualizar campos parciales
            if (dtoJson != null && !dtoJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                UserDetailDTO dto = objectMapper.readValue(dtoJson, UserDetailDTO.class);

                // Actualizar UserProfile solo si viene y es diferente
                if (dto.getUserId() != 0 && (userProfile == null || dto.getUserId() != userProfile.getUserId())) {
                    UserProfile newUserProfile = upS.listId(dto.getUserId());
                    if (newUserProfile == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario asociado no encontrado");
                    }
                    existingUDetail.setUserProfile(newUserProfile);
                    userProfile = newUserProfile;
                }

                // Actualizar otros campos que quieras, por ejemplo profilepicture si viene
                if (dto.getProfilepicture() != null) {
                    existingUDetail.setProfilepicture(dto.getProfilepicture());
                }

            }

            // Validar que UserProfile esté asignado antes de subir imagen
            if (userProfile == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El detalle de usuario no tiene perfil asociado");
            }

            // Procesar archivo de imagen opcional
            if (picture != null && !picture.isEmpty()) {
                // Eliminar imagen anterior en Firebase si existe
                if (existingUDetail.getProfilepicture() != null && !existingUDetail.getProfilepicture().isEmpty()) {
                    System.out.println("URL a eliminar: " + existingUDetail.getProfilepicture());
                    firebaseStorageService.deleteImage(existingUDetail.getProfilepicture());
                }
                // Subir nueva imagen a Firebase con carpeta por userId
                String imageUrl = firebaseStorageService.uploadImage(picture, userProfile.getUserId());
                existingUDetail.setProfilepicture(imageUrl);
            }

            // Guardar la información actualizada en la base de datos
            udS.update(existingUDetail); // save() detectará que es update por el ID presente

            return ResponseEntity.ok("Información del usuario actualizada correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo de imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la información del usuario: " + e.getMessage());
        }
    }


}

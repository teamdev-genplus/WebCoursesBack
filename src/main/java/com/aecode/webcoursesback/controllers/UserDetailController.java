package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserDetailDTO;
import com.aecode.webcoursesback.entities.*;
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
            if (existingUDetail == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Información del usuario no encontrada");
            }

            UserProfile userProfile = existingUDetail.getUserProfile();

            // Procesar datos enviados en JSON
            if (dtoJson != null && !dtoJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                UserDetailDTO dto = objectMapper.readValue(dtoJson, UserDetailDTO.class);

                // Actualizar campos del DTO
                if (dto.getUserId() != 0 && dto.getUserId() != userProfile.getUserId()) {
                    UserProfile newUserProfile = upS.listId(dto.getUserId());
                    if (newUserProfile == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario asociado no encontrado");
                    }
                    existingUDetail.setUserProfile(newUserProfile);
                    userProfile = newUserProfile; // Actualizar el perfil para usarlo en la ruta de la imagen
                }

                if (dto.getProfilepicture() != null) {
                    existingUDetail.setProfilepicture(dto.getProfilepicture());
                }
            }

            // Procesar archivo de imagen opcional
            if (picture != null && !picture.isEmpty()) {
                String userUploadDir = uploadDir + File.separator + "userdetail" + File.separator + userProfile.getUserId();
                Path userUploadPath = Paths.get(userUploadDir);

                if (!Files.exists(userUploadPath)) {
                    Files.createDirectories(userUploadPath);
                }

                String originalFilename = picture.getOriginalFilename();
                byte[] bytes = picture.getBytes();
                Path path = userUploadPath.resolve(originalFilename);
                Files.write(path, bytes);

                // Actualizar la ruta de la imagen
                existingUDetail.setProfilepicture("/uploads/userdetail/" + userProfile.getUserId() + "/" + originalFilename);
            }

            // Guardar la información actualizada en la base de datos
            udS.insert(existingUDetail);

            return ResponseEntity.ok("Información del usuario actualizada correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo de imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la información del usuario: " + e.getMessage());
        }
    }


}

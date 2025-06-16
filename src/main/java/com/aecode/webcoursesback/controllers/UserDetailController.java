package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserDetailDTO;
import com.aecode.webcoursesback.dtos.UserUpdateDTO;
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
    @Autowired
    private FirebaseStorageService firebaseStorageService;

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
                dto.setUserId(Long.valueOf(0)); // O asignar un valor por defecto
            }
            return dto;
        }).collect(Collectors.toList());
    }

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
            dto.setUserId(Long.valueOf(0)); // O asignar el valor que desees
        }

        return dto;
    }

    @GetMapping("/by-user/{userId}") // Nuevo endpoint para buscar por userId
    public UserDetailDTO findByUserId(@PathVariable Long userId) {
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

    @PatchMapping(value = "/{userId}/update-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProfilePicture(@PathVariable("userId") Long userId,
                                                       @RequestParam("image") MultipartFile imagen) {
        try {
            // Verificar que el perfil de usuario existe
            UserProfile userProfile = upS.listId(userId);
            if (userProfile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            // Obtener el UserDetail existente
            UserDetail userDetail = udS.findByUserId(userId);
            if (userDetail == null) {
                userDetail = new UserDetail();
                userDetail.setUserProfile(userProfile); // Relación con el UserProfile
            }

            // Generar una ruta segura para el nombre de usuario
            String fullName = userProfile.getFullname();
            String fullNameSafe = fullName != null ? fullName.replaceAll("[^a-zA-Z0-9]", "_") : "usuario";
            String path = "users/" + userId + "_" + fullNameSafe + "/images/";

            // Subir la imagen a Firebase
            String imageUrl = firebaseStorageService.uploadImage(imagen, path);

            // Establecer la nueva URL de la imagen en el UserDetail
            userDetail.setProfilepicture(imageUrl);

            // Guardar el UserDetail con la nueva imagen
            udS.update(userDetail);

            return ResponseEntity.ok("Imagen actualizada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }



}

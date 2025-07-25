package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.UserDetail;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.IUserDetailService;
import com.aecode.webcoursesback.services.IUserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userprofile")
public class UserProfileController {

    @Autowired
    private IUserProfileService upS;

    @Autowired
    private IUserDetailService udS;

    // Registro de un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationDTO dto) {
        try {
            upS.insert(dto);
            return ResponseEntity.ok("Perfil creado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Registro de un usuario de Clerk
    @PostMapping("/register")
    public ResponseEntity<String> registerUserClerk(@RequestBody UserClerkDTO dto) {
        try {
            upS.insertuserClerk(dto);
            return ResponseEntity.ok("Usuario de Clerk guardado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Autenticación de usuario existente
    @PostMapping("/login")
    public ResponseEntity<UserProfileDTO> login(@RequestBody LoginDTO dto) {
        UserProfile profile = upS.authenticateUser( dto);
        if (profile != null && profile.getPasswordHash().equals(dto.getPasswordHash())) {
            ModelMapper modelMapper = new ModelMapper(); // Crea una instancia de ModelMapper
            UserProfileDTO userProfileDTO = modelMapper.map(profile, UserProfileDTO.class); // Mapea de UserProfile a UserProfileDTO
            return ResponseEntity.ok(userProfileDTO); // Devuelve el objeto LoginDTO con el ID de perfil actualizado
        } else {
            return ResponseEntity.badRequest().body(null); // En caso de credenciales inválidas, puedes devolver null o un objeto vacío
        }
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Long id){upS.delete(id);}


    @PatchMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Long id, @RequestBody String jsonPayload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));

            UserUpdateDTO dto = objectMapper.readValue(jsonPayload, UserUpdateDTO.class);

            // Buscar UserProfile
            UserProfile existingUser = upS.listId(id);
            if (existingUser == null || existingUser.getUserId() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            // Actualizar campos de UserProfile si están presentes
            if (dto.getFullname() != null) existingUser.setFullname(dto.getFullname());
            if (dto.getEmail() != null) existingUser.setEmail(dto.getEmail());
            if (dto.getPassword() != null) existingUser.setPasswordHash(dto.getPassword());

            upS.update(existingUser);

            // Actualizar UserDetail si hay campos presentes
            boolean hasUserDetailData = dto.getPhoneNumber() != null ||
                    dto.getGender() != null ||
                    dto.getCountry() != null ||
                    dto.getProfession() != null ||
                    dto.getEducation() != null ||
                    dto.getLinkedin() != null ||
                    dto.getBirthdate() != null;

            if (hasUserDetailData) {
                // Cargar UserDetail existente
                UserDetail existingDetail = udS.findByUserId(id);
                if (existingDetail == null) {
                    // Si no existe detalle, crear uno nuevo
                    existingDetail = new UserDetail();
                    existingDetail.setUserProfile(existingUser);
                }

                // Actualizar solo campos no nulos
                if (dto.getPhoneNumber() != null) existingDetail.setPhoneNumber(dto.getPhoneNumber());
                if (dto.getGender() != null) existingDetail.setGender(dto.getGender());
                if (dto.getCountry() != null) existingDetail.setCountry(dto.getCountry());
                if (dto.getProfession() != null) existingDetail.setProfession(dto.getProfession());
                if (dto.getEducation() != null) existingDetail.setEducation(dto.getEducation());
                if (dto.getLinkedin() != null) existingDetail.setLinkedin(dto.getLinkedin());
                if (dto.getBirthdate() != null) existingDetail.setBirthdate(dto.getBirthdate());

                udS.update(existingDetail);
            }

            return ResponseEntity.ok("Usuario actualizado correctamente");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de JSON inválido: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping
    public ResponseEntity<List<UserUpdateDTO>> getAllProfiles() {
        List<UserUpdateDTO> profiles = upS.listusers();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserUpdateDTO> getProfileById(@PathVariable Long id) {
        try {
            UserUpdateDTO profile = upS.listusersId(id);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my-profile/{userId}")
    public ResponseEntity<MyProfileDTO> getMyProfile(@PathVariable Long userId) {
        MyProfileDTO dto = upS.getMyProfile(userId);
        return ResponseEntity.ok(dto);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    @PatchMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable("id") Long userId,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        try {
            // Llamar al servicio para cambiar la contraseña
            upS.changePassword(userId, currentPassword, newPassword);
            return ResponseEntity.ok("Contraseña actualizada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cambiar la contraseña: " + e.getMessage());
        }
    }

}

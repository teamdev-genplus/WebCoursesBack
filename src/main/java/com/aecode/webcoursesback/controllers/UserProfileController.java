package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.*;
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

    // Listado de todos los usuarios
//    @GetMapping("/list")
//    public List<UserProfileDTO> listUsers() {
//        ModelMapper modelMapper = new ModelMapper();
//
//        return upS.list().stream().map(user -> {
//            // Convertir el usuario al DTO de perfil
//            UserProfileDTO userProfileDTO = modelMapper.map(user, UserProfileDTO.class);
//
//            // Filtrar solo los UserProgressRwDTO con un workId válido (mayor a 0)
//            List<UserProgressRwDTO> filteredUserProgressRw = user.getUserprogressrw().stream()
//                    .filter(progress -> progress.getRw() != null && progress.getRw().getWorkId() > 0)
//                    .map(progress -> {
//                        // Aquí mapeamos el UserProgressRW a UserProgressRwDTO
//                        UserProgressRwDTO progressDTO = modelMapper.map(progress, UserProgressRwDTO.class);
//                        progressDTO.setWorkId(progress.getRw().getWorkId()); // Asignamos el workId
//
//                        return progressDTO;
//                    })
//                    .collect(Collectors.toList());
//
//            // Actualizamos el DTO de UserProfile con solo los elementos válidos de UserProgressRwDTO
//            userProfileDTO.setUserprogressrw(filteredUserProgressRw);
//
//            return userProfileDTO;
//        }).collect(Collectors.toList());
//    }
//
//    // Obtener un usuario por ID
//    @GetMapping("/{id}")
//    public UserProfileDTO listId(@PathVariable("id") Integer id) {
//        ModelMapper modelMapper = new ModelMapper();
//
//        // Buscar el usuario por ID
//        UserProfile user = upS.listId(id);
//        if (user == null) {
//            throw new RuntimeException("Usuario no encontrado"); // Manejo de error, opcional
//        }
//
//        // Convertir el usuario a UserProfileDTO
//        UserProfileDTO userProfileDTO = modelMapper.map(user, UserProfileDTO.class);
//
//        // Filtrar solo los UserProgressRwDTO con un workId válido (mayor a 0)
//        List<UserProgressRwDTO> filteredUserProgressRw = user.getUserprogressrw().stream()
//                .filter(progress -> progress.getRw() != null && progress.getRw().getWorkId() > 0)
//                .map(progress -> {
//                    // Mapear el UserProgressRW a UserProgressRwDTO
//                    UserProgressRwDTO progressDTO = modelMapper.map(progress, UserProgressRwDTO.class);
//                    progressDTO.setWorkId(progress.getRw().getWorkId()); // Asignar el workId
//
//                    return progressDTO;
//                })
//                .collect(Collectors.toList());
//
//        // Actualizar el UserProfileDTO con los elementos filtrados
//        userProfileDTO.setUserprogressrw(filteredUserProgressRw);
//
//        return userProfileDTO;
//    }


    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){upS.delete(id);}


    @PatchMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Integer id, @RequestBody String jsonPayload) {
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
                UserUpdateDTO detailDTO = new UserUpdateDTO();
                detailDTO.setUserId(id); // el ID del UserProfile
                detailDTO.setPhoneNumber(dto.getPhoneNumber());
                detailDTO.setGender(dto.getGender());
                detailDTO.setCountry(dto.getCountry());
                detailDTO.setProfession(dto.getProfession());
                detailDTO.setEducation(dto.getEducation());
                detailDTO.setLinkedin(dto.getLinkedin());
                detailDTO.setBirthdate(dto.getBirthdate());

                udS.updateUserDetail(detailDTO);
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
    public ResponseEntity<UserUpdateDTO> getProfileById(@PathVariable int id) {
        try {
            UserUpdateDTO profile = upS.listusersId(id);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    @PatchMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable("id") int userId,
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

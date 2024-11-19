package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.LoginDTO;
import com.aecode.webcoursesback.dtos.UserProfileDTO;
import com.aecode.webcoursesback.dtos.UserProgressRwDTO;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userprofile")
public class UserProfileController {

    @Autowired
    private IUserProfileService upS;

    // Registro de un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserProfileDTO userProfileDTO) {
        try {
            upS.insert(userProfileDTO);
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
    @GetMapping("/list")
    public List<UserProfileDTO> listUsers() {
        ModelMapper modelMapper = new ModelMapper();

        return upS.list().stream().map(user -> {
            // Convertir el usuario al DTO de perfil
            UserProfileDTO userProfileDTO = modelMapper.map(user, UserProfileDTO.class);

            // Filtrar solo los UserProgressRwDTO con un workId válido (mayor a 0)
            List<UserProgressRwDTO> filteredUserProgressRw = user.getUserprogressrw().stream()
                    .filter(progress -> progress.getRw() != null && progress.getRw().getWorkId() > 0)
                    .map(progress -> {
                        // Aquí mapeamos el UserProgressRW a UserProgressRwDTO
                        UserProgressRwDTO progressDTO = modelMapper.map(progress, UserProgressRwDTO.class);
                        progressDTO.setWorkId(progress.getRw().getWorkId()); // Asignamos el workId

                        return progressDTO;
                    })
                    .collect(Collectors.toList());

            // Actualizamos el DTO de UserProfile con solo los elementos válidos de UserProgressRwDTO
            userProfileDTO.setUserprogressrw(filteredUserProgressRw);

            return userProfileDTO;
        }).collect(Collectors.toList());
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public UserProfileDTO listId(@PathVariable("id") Integer id) {
        ModelMapper modelMapper = new ModelMapper();

        // Buscar el usuario por ID
        UserProfile user = upS.listId(id);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado"); // Manejo de error, opcional
        }

        // Convertir el usuario a UserProfileDTO
        UserProfileDTO userProfileDTO = modelMapper.map(user, UserProfileDTO.class);

        // Filtrar solo los UserProgressRwDTO con un workId válido (mayor a 0)
        List<UserProgressRwDTO> filteredUserProgressRw = user.getUserprogressrw().stream()
                .filter(progress -> progress.getRw() != null && progress.getRw().getWorkId() > 0)
                .map(progress -> {
                    // Mapear el UserProgressRW a UserProgressRwDTO
                    UserProgressRwDTO progressDTO = modelMapper.map(progress, UserProgressRwDTO.class);
                    progressDTO.setWorkId(progress.getRw().getWorkId()); // Asignar el workId

                    return progressDTO;
                })
                .collect(Collectors.toList());

        // Actualizar el UserProfileDTO con los elementos filtrados
        userProfileDTO.setUserprogressrw(filteredUserProgressRw);

        return userProfileDTO;
    }


    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){upS.delete(id);}


    // Actualizar un usuario
    @PutMapping
    public void update(@RequestBody UserProfileDTO dto) {
        ModelMapper m = new ModelMapper();
        UserProfile p = m.map(dto, UserProfile.class);
        upS.update(p);
    }

}

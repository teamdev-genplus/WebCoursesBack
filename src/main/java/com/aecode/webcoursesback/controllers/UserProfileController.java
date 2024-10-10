package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.LoginDTO;
import com.aecode.webcoursesback.dtos.UserProfileDTO;
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
    public ResponseEntity<UserProfile> login(@RequestBody LoginDTO dto) {
        UserProfile profile = upS.authenticateUser( dto);
        if (profile != null && profile.getPasswordHash().equals(dto.getPasswordHash())) {
            dto.setUserId(profile.getUserId());
            return ResponseEntity.ok(profile); // Devuelve el objeto LoginDTO con el ID de perfil actualizado
        } else {
            return ResponseEntity.badRequest().body(null); // En caso de credenciales inválidas, puedes devolver null o un objeto vacío
        }
    }

    // Listado de todos los usuarios
    @GetMapping("/list")
    public List<UserProfileDTO> listUsers() {
        return upS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, UserProfileDTO.class);
        }).collect(Collectors.toList());
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public UserProfileDTO listId(@PathVariable("id")Integer id) {
        ModelMapper m=new ModelMapper();
        UserProfileDTO dto=m.map(upS.listId(id),UserProfileDTO.class);
        return dto;
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

    @GetMapping("/searchByEmail")
    public ResponseEntity<List<UserProfileDTO>> searchByEmail(@RequestParam String emailFragment) {
        // Buscar los usuarios cuyo email contenga el fragmento proporcionado
        List<UserProfile> users = upS.findByPartialEmail(emailFragment);
        List<UserProfileDTO> result = users.stream()
                .map(user -> {
                    ModelMapper mapper = new ModelMapper();
                    return mapper.map(user, UserProfileDTO.class);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/withAccess")
    public ResponseEntity<List<UserProfileDTO>> listUsersWithAccess() {
        List<UserProfile> usersWithAccess = upS.findUsersWithAccess();
        List<UserProfileDTO> result = usersWithAccess.stream()
                .map(user -> {
                    ModelMapper mapper = new ModelMapper();
                    return mapper.map(user, UserProfileDTO.class);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // Endpoint para listar usuarios con hasAccess = false
    @GetMapping("/withoutAccess")
    public ResponseEntity<List<UserProfileDTO>> listUsersWithoutAccess() {
        List<UserProfile> usersWithoutAccess = upS.findUsersWithoutAccess();
        List<UserProfileDTO> result = usersWithoutAccess.stream()
                .map(user -> {
                    ModelMapper mapper = new ModelMapper();
                    return mapper.map(user, UserProfileDTO.class);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}

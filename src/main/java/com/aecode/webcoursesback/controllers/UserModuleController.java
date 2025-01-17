package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.UserModuleDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserModuleAccess;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.IModuleService;
import com.aecode.webcoursesback.services.IUserModuleService;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usermodule")
public class UserModuleController {
    @Autowired
    IUserModuleService umS;
    @Autowired
    private IUserProfileService pS;
    @Autowired
    private IModuleService mS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody UserModuleDTO dto) {

        // Cargar manualmente las entidades UserProfile y Session
        UserProfile user = pS.listId(dto.getUserId());
        Module module = mS.listId(dto.getModuleId());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }
        if (module == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Modulo no encontrado");
        }

        // Mapear el DTO a la entidad
        UserModuleAccess usermodule = new UserModuleAccess();
        usermodule.setUserProfile(user); // Asignar el UserProfile
        usermodule.setModule(module); // Asignar el modulo

        // Guardar en la base de datos
        umS.insert(usermodule);

        return ResponseEntity.ok("Modulo del usuario guardado correctamente");
    }

    @GetMapping
    public List<UserModuleDTO> list() {
        return umS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, UserModuleDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        umS.delete(id);
    }

    @GetMapping("/{id}")
    public UserModuleDTO listId(@PathVariable("id") Integer id) {
        ModelMapper m = new ModelMapper();
        UserModuleDTO dto = m.map(umS.listId(id), UserModuleDTO.class);
        return dto;
    }

    @PutMapping
    public void update(@RequestBody UserModuleDTO dto) {
        ModelMapper m = new ModelMapper();
        UserModuleAccess uma = m.map(dto, UserModuleAccess.class);
        // Asegurarse de cargar los objetos UserProfile y modulo
        UserProfile user = pS.listId(dto.getUserId());
        Module module = mS.listId(dto.getModuleId());

        uma.setUserProfile(user);
        uma.setModule(module);

        umS.insert(uma);
    }

}

package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserProgressDTO;
import com.aecode.webcoursesback.entities.Class;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.entities.UserProgress;
import com.aecode.webcoursesback.services.IClassService;
import com.aecode.webcoursesback.services.IUserProfileService;
import com.aecode.webcoursesback.services.IUserProgressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userProgress")
public class UserProgressController {

    @Autowired
    private IUserProgressService upS;
    @Autowired
    private IUserProfileService pS;
    @Autowired
    private IClassService cS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody UserProgressDTO dto){
        ModelMapper m = new ModelMapper();

        // Cargar manualmente las entidades UserProfile y Class
        UserProfile user = pS.listId(dto.getUserId());
        Class aClass = cS.listId(dto.getClassId());

        if (user == null || aClass == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario o clase no encontrados");
        }

        // Mapear el DTO a la entidad
        UserProgress userProgress = m.map(dto, UserProgress.class);
        userProgress.setUserProfile(user); // Asignar el UserProfile
        userProgress.setClasses(aClass); // Asignar la Class

        upS.insert(userProgress);
        return ResponseEntity.ok("Progreso guardado correctamente");
    }

    @GetMapping
    public List<UserProgressDTO> list() {
        return upS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, UserProgressDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){upS.delete(id);}

    @GetMapping("/{id}")
    public UserProgressDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        UserProgressDTO dto=m.map(upS.listId(id),UserProgressDTO.class);
        return dto;
    }
    @PutMapping
    public void update(@RequestBody UserProgressDTO dto) {
        ModelMapper m = new ModelMapper();
        UserProgress u = m.map(dto, UserProgress.class);

        // Asegurarse de cargar los objetos UserProfile y Class
        UserProfile user = pS.listId(dto.getUserId());
        Class aClass = cS.listId(dto.getClassId());

        u.setUserProfile(user);
        u.setClasses(aClass);

        upS.insert(u);
    }
}

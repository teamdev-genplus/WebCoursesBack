package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserProgressDTO;
import com.aecode.webcoursesback.entities.Session;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.entities.UserProgressSession;
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

        // Cargar manualmente las entidades UserProfile y Session
        UserProfile user = pS.listId(dto.getUserId());
        Session aSession = cS.listId(dto.getClassId());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }
        if (aSession == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Clase no encontrada");
        }

        // Mapear el DTO a la entidad
        UserProgressSession userProgressSession = new UserProgressSession();
        userProgressSession.setUserProfile(user); // Asignar el UserProfile
        userProgressSession.setSession(aSession);   // Asignar la Session
        userProgressSession.setCompleted(dto.isCompleted()); // Asignar si está completado

        // Guardar en la base de datos
        upS.insert(userProgressSession);

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
        UserProgressSession u = m.map(dto, UserProgressSession.class);

        // Asegurarse de cargar los objetos UserProfile y Session
        UserProfile user = pS.listId(dto.getUserId());
        Session aSession = cS.listId(dto.getClassId());

        u.setUserProfile(user);
        u.setSession(aSession);

        upS.insert(u);
    }
}

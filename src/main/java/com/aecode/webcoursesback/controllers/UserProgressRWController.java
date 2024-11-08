package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserProgressRwDTO;
import com.aecode.webcoursesback.entities.RelatedWork;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.entities.UserProgressRW;
import com.aecode.webcoursesback.services.IRelatedWorkService;
import com.aecode.webcoursesback.services.IUserProfileService;
import com.aecode.webcoursesback.services.IUserProgressRwService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/progressRW")
public class UserProgressRWController {
    @Autowired
    private IUserProgressRwService upuS;
    @Autowired
    private IUserProfileService pS;
    @Autowired
    private IRelatedWorkService rwS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody UserProgressRwDTO dto){
        ModelMapper m = new ModelMapper();

        // Cargar manualmente las entidades UserProfile y Session
        UserProfile user = pS.listId(dto.getUserId());
        RelatedWork rw = rwS.listId(dto.getWorkId());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }
        if (rw == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Trabajo relacionado no encontrada");
        }

        // Mapear el DTO a la entidad
        UserProgressRW userProgressRW = new UserProgressRW();
        userProgressRW.setUserProfile(user); // Asignar el UserProfile
        userProgressRW.setRw(rw);   // Asignar el trabajo relacionado
        userProgressRW.setCompleted(dto.isCompleted()); // Asignar si está completado

        // Guardar en la base de datos
        upuS.insert(userProgressRW);

        return ResponseEntity.ok("Progreso guardado correctamente");
    }

    @GetMapping
    public List<UserProgressRwDTO> list() {
        ModelMapper modelMapper = new ModelMapper();

        // Configuración específica para el mapeo de workId
        modelMapper.typeMap(UserProgressRW.class, UserProgressRwDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getRw().getWorkId(), UserProgressRwDTO::setWorkId);
            mapper.map(src -> src.getUserProfile().getUserId(), UserProgressRwDTO::setUserId);  // Para asegurar el userId también
        });

        // Mapear la lista de UserProgressRW a UserProgressRwDTO
        return upuS.list().stream()
                .map(x -> modelMapper.map(x, UserProgressRwDTO.class))
                .collect(Collectors.toList());
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){upuS.delete(id);}

    @GetMapping("/{id}")
    public UserProgressRwDTO listId(@PathVariable("id") Integer id) {
        ModelMapper modelMapper = new ModelMapper();

        // Configuración específica para mapear workId y userId
        modelMapper.typeMap(UserProgressRW.class, UserProgressRwDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getRw().getWorkId(), UserProgressRwDTO::setWorkId);
            mapper.map(src -> src.getUserProfile().getUserId(), UserProgressRwDTO::setUserId);
        });

        // Mapear el objeto UserProgressRW a UserProgressRwDTO
        UserProgressRW userProgressRW = upuS.listId(id);
        UserProgressRwDTO dto = modelMapper.map(userProgressRW, UserProgressRwDTO.class);

        return dto;
    }

    @PutMapping
    public void update(@RequestBody UserProgressRwDTO dto) {
        ModelMapper m = new ModelMapper();
        UserProgressRW u = m.map(dto, UserProgressRW.class);

        // Asegurarse de cargar los objetos UserProfile y Unit
        UserProfile user = pS.listId(dto.getUserId());
        RelatedWork rw = rwS.listId(dto.getWorkId());

        u.setUserProfile(user);
        u.setRw(rw);

        upuS.insert(u);
    }
}

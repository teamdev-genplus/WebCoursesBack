package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserSecCourseDTO;
import com.aecode.webcoursesback.entities.SecondaryCourses;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.entities.UserSecCourseAccess;
import com.aecode.webcoursesback.services.EmailSenderService;
import com.aecode.webcoursesback.services.ISecondCourseService;
import com.aecode.webcoursesback.services.IUserProfileService;
import com.aecode.webcoursesback.services.IUserSecCourseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userseccourse")
public class UserSecCourseController {


    @Autowired
    IUserSecCourseService uscS;
    @Autowired
    private IUserProfileService pS;
    @Autowired
    private ISecondCourseService scS;

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody UserSecCourseDTO dto) {

        // Cargar manualmente las entidades UserProfile y SecondaryCourses
        UserProfile user = pS.listId(dto.getUserId());
        SecondaryCourses seccourse = scS.listId(dto.getSeccourseId());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }
        if (seccourse == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Curso Secundario no encontrado");
        }

        // Mapear el DTO a la entidad
        UserSecCourseAccess userseccourse = new UserSecCourseAccess();
        userseccourse.setUserProfile(user); // Asignar el UserProfile
        userseccourse.setSeccourse(seccourse); // Asignar el curso secundario

        // Guardar en la base de datos
        uscS.insert(userseccourse);

        //ENVIO DE CORREOS
        // Preparar contenido email usuario
        String userBody = String.format("Hola %s,\n\nGracias por comprar el curso: %s.\nFecha: %s\n\nSaludos,\nEquipo Aecode",
                user.getFullname(), seccourse.getTitle(), LocalDateTime.now());

        emailSenderService.sendEmail(user.getEmail(), "Confirmación de compra", userBody);

        // Preparar contenido email empresa
        String companyBody = String.format("El usuario %s (%s) ha comprado el curso: %s.\nFecha: %s",
                user.getFullname(), user.getEmail(), seccourse.getTitle(), LocalDateTime.now());

        emailSenderService.sendEmail("manuel.wrk.10@gmail.com\n", "Nueva compra de curso", companyBody);

        return ResponseEntity.ok("Curso Secundario del usuario guardado correctamente");
    }

    @GetMapping
    public List<UserSecCourseDTO> list() {
        return uscS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, UserSecCourseDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        uscS.delete(id);
    }

    @GetMapping("/{id}")
    public UserSecCourseDTO listId(@PathVariable("id") Integer id) {
        ModelMapper m = new ModelMapper();
        UserSecCourseDTO dto = m.map(uscS.listId(id), UserSecCourseDTO.class);
        return dto;
    }

    @PutMapping
    public void update(@RequestBody UserSecCourseDTO dto) {
        ModelMapper m = new ModelMapper();
        UserSecCourseAccess usca = m.map(dto, UserSecCourseAccess.class);
        // Asegurarse de cargar los objetos UserProfile y SecondaryCourses
        UserProfile user = pS.listId(dto.getUserId());
        SecondaryCourses seccourse = scS.listId(dto.getSeccourseId());

        usca.setUserProfile(user);
        usca.setSeccourse(seccourse);

        uscS.insert(usca);
    }

}

package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.ClassQuestionDTO;
import com.aecode.webcoursesback.entities.Class;
import com.aecode.webcoursesback.entities.ClassQuestion;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.IClassRepo;
import com.aecode.webcoursesback.services.IClassQuestionService;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/classquestion")
public class ClassQuestionController {

    @Autowired
    private IClassQuestionService qS;
    @Autowired
    private IClassRepo classRepository;

    @Autowired
    private IUserProfileService upS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody ClassQuestionDTO dto) {
        ModelMapper m = new ModelMapper();
        ClassQuestion q = m.map(dto, ClassQuestion.class);

        // Buscar la entidad Class usando el classId del DTO
        Class aclass = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + dto.getClassId()));

        // Asignar la entidad Class encontrada a la entidad ClassQuestion
        q.setAclass(aclass);

        // Insertar la entidad usando el servicio
        qS.insert(q);

        return ResponseEntity.status(201).body("created successfully");

    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam String email) {
        // Verificar si el usuario tiene acceso
        UserProfile user = upS.findByEmail(email);
        if (user == null || !user.isHasAccess()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: No access to class questions.");
        }

        // Si tiene acceso, devolver las preguntas de la clase
        ModelMapper m = new ModelMapper();
        List<ClassQuestion> q = qS.list();
        List<ClassQuestionDTO> questionDTOs = q.stream()
                .map(question -> m.map(question, ClassQuestionDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(questionDTOs);
    }

        @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){qS.delete(id);}

    @GetMapping("/{id}")
    public ClassQuestionDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        ClassQuestionDTO dto=m.map(qS.listId(id),ClassQuestionDTO.class);
        return dto;
    }
    @PutMapping
    public void update(@RequestBody ClassQuestionDTO dto) {
        ModelMapper m = new ModelMapper();
        ClassQuestion q = m.map(dto, ClassQuestion.class);
        qS.insert(q);
    }
}

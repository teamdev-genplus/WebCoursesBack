package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.ClassQuestionDTO;
import com.aecode.webcoursesback.entities.Session;
import com.aecode.webcoursesback.entities.SessionTest;
import com.aecode.webcoursesback.repositories.IClassRepo;
import com.aecode.webcoursesback.services.IClassQuestionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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


    @PostMapping
    public ResponseEntity<String> insert(@RequestBody ClassQuestionDTO dto) {
        ModelMapper m = new ModelMapper();
        SessionTest q = m.map(dto, SessionTest.class);

        // Buscar la entidad Session usando el classId del DTO
        Session aclass = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + dto.getClassId()));

        // Asignar la entidad Session encontrada a la entidad SessionTest
        q.setSession(aclass);

        // Insertar la entidad usando el servicio
        qS.insert(q);

        return ResponseEntity.status(201).body("created successfully");

    }

    @GetMapping
    public List<ClassQuestionDTO> list() {
        return qS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, ClassQuestionDTO.class);
        }).collect(Collectors.toList());
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
        SessionTest q = m.map(dto, SessionTest.class);
        qS.insert(q);
    }
}

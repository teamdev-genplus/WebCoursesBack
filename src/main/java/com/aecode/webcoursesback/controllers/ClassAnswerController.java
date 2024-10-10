package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.ClassAnswerDTO;
import com.aecode.webcoursesback.entities.Class;
import com.aecode.webcoursesback.entities.ClassAnswer;
import com.aecode.webcoursesback.entities.ClassQuestion;
import com.aecode.webcoursesback.repositories.IClassQuestionRepo;
import com.aecode.webcoursesback.services.IClassAnswerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/classanswer")
public class ClassAnswerController {
    @Autowired
    private IClassAnswerService aS;
    @Autowired
    private IClassQuestionRepo cqR;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody ClassAnswerDTO dto) {
        ModelMapper m = new ModelMapper();
        ClassAnswer a = m.map(dto, ClassAnswer.class);

        // Buscar la entidad Class usando el classId del DTO
        ClassQuestion classquestion = cqR.findById(dto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("ClassQuestion not found with id: " + dto.getQuestionId()));

        // Asignar la entidad Classquestion encontrada a la entidad ClassAnswer
        a.setClassquestion(classquestion);

        // Insertar la entidad usando el servicio
        aS.insert(a);

        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public List<ClassAnswerDTO> list() {
        ModelMapper m = new ModelMapper();
        List<ClassAnswer> a = aS.list();
        return a.stream().map(answer -> {
            // Convertir la entidad a DTO y asignar el questionId manualmente
            ClassAnswerDTO dto = m.map(answer, ClassAnswerDTO.class);
            dto.setQuestionId(answer.getClassquestion().getQuestionId());  // Asignar manualmente el questionId
            return dto;
        }).collect(Collectors.toList());
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){aS.delete(id);}

    @GetMapping("/{id}")
    public ClassAnswerDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        ClassAnswerDTO dto=m.map(aS.listId(id),ClassAnswerDTO.class);
        return dto;
    }
    @PutMapping
    public void update(@RequestBody ClassAnswerDTO dto) {
        ModelMapper m = new ModelMapper();
        ClassAnswer a = m.map(dto, ClassAnswer.class);
        aS.insert(a);
    }

}

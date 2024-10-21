package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.ClassAnswerDTO;
import com.aecode.webcoursesback.entities.SessionAnswer;
import com.aecode.webcoursesback.entities.SessionTest;
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
        SessionAnswer a = m.map(dto, SessionAnswer.class);

        // Buscar la entidad Session usando el classId del DTO
        SessionTest s = cqR.findById(dto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("SessionTest not found with id: " + dto.getQuestionId()));

        // Asignar la entidad Classquestion encontrada a la entidad SessionAnswer
        a.setSessiontest(s);

        // Insertar la entidad usando el servicio
        aS.insert(a);

        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public List<ClassAnswerDTO> list() {
        ModelMapper m = new ModelMapper();
        List<SessionAnswer> a = aS.list();
        return a.stream().map(answer -> {
            // Convertir la entidad a DTO y asignar el questionId manualmente
            ClassAnswerDTO dto = m.map(answer, ClassAnswerDTO.class);
            dto.setQuestionId(answer.getSessiontest().getTestId());  // Asignar manualmente el questionId
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
        SessionAnswer a = m.map(dto, SessionAnswer.class);
        aS.insert(a);
    }

}

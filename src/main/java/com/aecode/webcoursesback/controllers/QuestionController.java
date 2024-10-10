package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.QuestionDTO;
import com.aecode.webcoursesback.entities.Question;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.IQuestionService;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private IQuestionService qS;

    @Autowired
    private IUserProfileService upS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody QuestionDTO dto) {
        ModelMapper m = new ModelMapper();
        Question q = m.map(dto, Question.class);
        qS.insert(q);
        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam String email) {
        // Verificar si el usuario tiene acceso
        UserProfile user = upS.findByEmail(email);
        if (user == null || !user.isHasAccess()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: No access to questions.");
        }

        // Si tiene acceso, devolver las preguntas
        ModelMapper m = new ModelMapper();
        List<Question> q = qS.list();
        List<QuestionDTO> questionDTOs = q.stream()
                .map(question -> m.map(question, QuestionDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(questionDTOs);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){qS.delete(id);}

    @GetMapping("/{id}")
    public QuestionDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        QuestionDTO dto=m.map(qS.listId(id),QuestionDTO.class);
        return dto;
    }
    @PutMapping
    public void update(@RequestBody QuestionDTO dto) {
        ModelMapper m = new ModelMapper();
        Question q = m.map(dto, Question.class);
        qS.insert(q);
    }
}

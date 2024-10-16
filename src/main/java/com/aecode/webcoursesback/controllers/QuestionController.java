package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.QuestionDTO;
import com.aecode.webcoursesback.entities.Question;
import com.aecode.webcoursesback.services.IQuestionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private IQuestionService qS;


    @PostMapping
    public ResponseEntity<String> insert(@RequestBody QuestionDTO dto) {
        ModelMapper m = new ModelMapper();
        Question q = m.map(dto, Question.class);
        qS.insert(q);
        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public List<QuestionDTO> list() {
        return qS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, QuestionDTO.class);
        }).collect(Collectors.toList());
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

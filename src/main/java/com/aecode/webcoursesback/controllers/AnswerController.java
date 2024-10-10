package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.AnswerDTO;
import com.aecode.webcoursesback.entities.Answer;
import com.aecode.webcoursesback.services.IAnswerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/answer")
public class AnswerController {

    @Autowired
    private IAnswerService aS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody AnswerDTO dto) {
        ModelMapper m = new ModelMapper();
        Answer a = m.map(dto, Answer.class);
        aS.insert(a);
        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public List<AnswerDTO> list() {
        ModelMapper m = new ModelMapper();
        List<Answer> a = aS.list();
        return a.stream()
                .map(answer -> m.map(answer, AnswerDTO.class))
                .collect(Collectors.toList());
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){aS.delete(id);}

    @GetMapping("/{id}")
    public AnswerDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        AnswerDTO dto=m.map(aS.listId(id),AnswerDTO.class);
        return dto;
    }
    @PutMapping
    public void update(@RequestBody AnswerDTO dto) {
        ModelMapper m = new ModelMapper();
        Answer a = m.map(dto, Answer.class);
        aS.insert(a);
    }

}

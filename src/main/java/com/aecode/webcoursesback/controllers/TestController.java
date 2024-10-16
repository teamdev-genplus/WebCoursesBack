package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.TestDTO;
import com.aecode.webcoursesback.entities.Test;
import com.aecode.webcoursesback.services.ITestService;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private ITestService tS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody TestDTO dto) {
        ModelMapper m = new ModelMapper();
        Test t = m.map(dto, Test.class);
        tS.insert(t);
        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public List<TestDTO> list() {
        return tS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, TestDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){tS.delete(id);}

    @GetMapping("/{id}")
    public TestDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        TestDTO dto=m.map(tS.listId(id),TestDTO.class);
        return dto;
    }
    @PutMapping
    public void update(@RequestBody TestDTO dto) {
        ModelMapper m = new ModelMapper();
        Test t = m.map(dto, Test.class);
        tS.insert(t);
    }
}

package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserProgressDTO;
import com.aecode.webcoursesback.entities.UserProgress;
import com.aecode.webcoursesback.services.IUserProgressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userProgress")
public class UserProgressController {

    @Autowired
    private IUserProgressService upS;

    @PostMapping
    public void insert(@RequestBody UserProgressDTO dto){
        ModelMapper m=new ModelMapper();
        UserProgress u= m.map(dto,UserProgress.class);
        upS.insert(u);
    }

    @GetMapping
    public List<UserProgressDTO> list() {
        return upS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, UserProgressDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){upS.delete(id);}

    @GetMapping("/{id}")
    public UserProgressDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        UserProgressDTO dto=m.map(upS.listId(id),UserProgressDTO.class);
        return dto;
    }
    @PutMapping
    public void update(@RequestBody UserProgressDTO dto) {
        ModelMapper m = new ModelMapper();
        UserProgress u = m.map(dto, UserProgress.class);
        upS.insert(u);
    }
}

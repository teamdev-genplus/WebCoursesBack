package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.ClassDTO;
import com.aecode.webcoursesback.dtos.CourseDTO;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.entities.Class;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.IModuleService;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/module")
public class ModuleController {
    @Autowired
    private IModuleService mS;

    @Autowired
    private IUserProfileService upS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody ModuleDTO dto) {
        ModelMapper m = new ModelMapper();
        Module module = m.map(dto, Module.class);
        mS.insert(module);
        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam String email) {
        // Verificar si el usuario tiene acceso
        UserProfile user = upS.findByEmail(email);
        if (user == null || !user.isHasAccess()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: No access to modules.");
        }

        ModelMapper m = new ModelMapper();
        List<ModuleDTO> moduleDTOs = mS.list().stream()
                .map(module -> {
                    ModuleDTO dto = m.map(module, ModuleDTO.class);
                    dto.setClasses(module.getClasses().stream()
                            .sorted(Comparator.comparing(Class::getClassId))
                            .map(classEntity -> m.map(classEntity, ClassDTO.class))
                            .collect(Collectors.toCollection(LinkedHashSet::new)));

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(moduleDTOs);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){mS.delete(id);}

    @GetMapping("/{id}")
    public ModuleDTO listId(@PathVariable("id")Integer id){
        ModelMapper m = new ModelMapper();
        Module module = mS.listId(id);
        ModuleDTO dto = m.map(module, ModuleDTO.class);
        dto.setClasses(module.getClasses().stream()
                .sorted(Comparator.comparing(Class::getClassId))
                .map(classEntity -> m.map(classEntity, ClassDTO.class))
                .collect(Collectors.toCollection(LinkedHashSet::new)));

        return dto;
    }
    @PutMapping
    public void update(@RequestBody ModuleDTO dto) {
        ModelMapper m = new ModelMapper();
        Module module = m.map(dto, Module.class);
        mS.insert(module);
    }
}

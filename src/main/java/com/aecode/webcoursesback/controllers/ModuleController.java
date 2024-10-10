package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.IModuleService;
import com.aecode.webcoursesback.services.IUserProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

        // Si tiene acceso, devolver los módulos
        ModelMapper m = new ModelMapper();
        List<Module> modules = mS.list();
        List<ModuleDTO> moduleDTOs = modules.stream()
                .map(module -> m.map(module, ModuleDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(moduleDTOs);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){mS.delete(id);}

    @GetMapping("/{id}")
    public ModuleDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        ModuleDTO dto=m.map(mS.listId(id),ModuleDTO.class);
        return dto;
    }
    @PutMapping
    public void update(@RequestBody ModuleDTO dto) {
        ModelMapper m = new ModelMapper();
        Module module = m.map(dto, Module.class);
        mS.insert(module);
    }
}

package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.ClassDTO;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.dtos.TestDTO;
import com.aecode.webcoursesback.entities.Class;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.services.IModuleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/module")
public class ModuleController {
    @Autowired
    private IModuleService mS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody ModuleDTO dto) {
        ModelMapper m = new ModelMapper();
        Module module = m.map(dto, Module.class);
        mS.insert(module);
        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public List<ModuleDTO> list() {
        return mS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, ModuleDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){mS.delete(id);}

    @GetMapping("/{id}")
    public ModuleDTO listId(@PathVariable("id")Integer id){
        ModelMapper m = new ModelMapper();
        Module module = mS.listId(id);
        ModuleDTO dto = m.map(module, ModuleDTO.class);

        // Mapear el test si existe
        if (module.getTest() != null) {
            dto.setTest(m.map(module.getTest(), TestDTO.class));
        }

        dto.setClasses(module.getClasses().stream()
                .sorted(Comparator.comparing(Class::getClassId)) // Ordenar clases por 'classId'
                .map(classEntity -> m.map(classEntity, ClassDTO.class)) // Mapear las clases a DTO
                .collect(Collectors.toList())); // Usar List para garantizar el orden

        return dto; // Devuelve el módulo DTO
    }
    @PutMapping
    public void update(@RequestBody ModuleDTO dto) {
        ModelMapper m = new ModelMapper();
        Module module = m.map(dto, Module.class);
        mS.insert(module);
    }
}

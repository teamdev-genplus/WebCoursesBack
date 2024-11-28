package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.ModuleDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.services.ICourseService;
import com.aecode.webcoursesback.services.IModuleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/module")
public class ModuleController {
    @Autowired
    private IModuleService mS;
    @Autowired
    private ICourseService cS;

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
        ModelMapper m=new ModelMapper();
        ModuleDTO dto=m.map(mS.listId(id),ModuleDTO.class);
        return dto;
    }
    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Integer id, @RequestBody ModuleDTO moduleDTO) {
        try {
            // Obtener el curso existente por ID
            Module existingModule = mS.listId(id);
            if (moduleDTO.getTitle() != null) {
                existingModule.setTitle(moduleDTO.getTitle());
            }
            if (moduleDTO.getVideoUrl() != null) {
                existingModule.setVideoUrl(moduleDTO.getVideoUrl());
            }
            if (moduleDTO.getOrderNumber() != 0) {
                existingModule.setOrderNumber(moduleDTO.getOrderNumber());
            }
            if (moduleDTO.getCourseId() != 0) {
                // Relacionar el módulo con otro curso si el curso ID es diferente
                Course course = cS.listId(moduleDTO.getCourseId());
                if (course == null || course.getCourseId() == 0) {
                    return ResponseEntity.status(404).body("Curso asociado no encontrado");
                }
                existingModule.setCourse(course);
            }


            // Guardar los cambios
            mS.insert(existingModule);

            return ResponseEntity.ok("Modulo actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el modulo: " + e.getMessage());
        }
    }

    @GetMapping("/by-course")
    public List<ModuleDTO> getModulesByCourseTitle(@RequestParam("title") String courseTitle) {
        List<Module> modules = mS.findModulesByCourseTitle(courseTitle);

        if (modules.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron modulos para el curso especificado");
        }

        // Convertir la lista de sesiones a SessionDTO
        ModelMapper modelMapper = new ModelMapper();
        return modules.stream().map(module -> {
            ModuleDTO dto = modelMapper.map(module, ModuleDTO.class);

            return dto;
        }).collect(Collectors.toList());
    }
}

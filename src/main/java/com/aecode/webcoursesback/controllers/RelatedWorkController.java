package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.RelatedWorkDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.RelatedWork;
import com.aecode.webcoursesback.services.IModuleService;
import com.aecode.webcoursesback.services.IRelatedWorkService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/relatedwork")
public class RelatedWorkController {
    @Autowired
    private IRelatedWorkService rwS;
    @Autowired
    private IModuleService mS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody RelatedWorkDTO dto) {
        ModelMapper m = new ModelMapper();
        RelatedWork rw = m.map(dto, RelatedWork.class);
        rwS.insert(rw);
        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public List<RelatedWorkDTO> list() {
        return rwS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, RelatedWorkDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){rwS.delete(id);}

    @GetMapping("/{id}")
    public RelatedWorkDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        RelatedWorkDTO dto=m.map(rwS.listId(id),RelatedWorkDTO.class);
        return dto;
    }
    @PatchMapping("/{id}")
    public ResponseEntity<String> updatePartial(
            @PathVariable("id") Integer id,
            @RequestBody RelatedWorkDTO dto) {

        try {
            // Obtener el trabajo relacionado existente por ID
            RelatedWork existingWork = rwS.listId(id);
            if (existingWork == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trabajo relacionado no encontrado");
            }

            // Actualizar campos solo si están presentes en el DTO
            if (dto.getFormUrl() != null) {
                existingWork.setFormUrl(dto.getFormUrl());
            }
            if (dto.getTitle() != null) {
                existingWork.setTitle(dto.getTitle());
            }
            if (dto.getModuleId() != 0) {
                Module module = mS.listId(dto.getModuleId());
                if (module == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Módulo asociado no encontrado");
                }
                existingWork.setModule(module);
            }

            // Guardar los cambios en la base de datos
            rwS.insert(existingWork);

            return ResponseEntity.ok("Trabajo relacionado actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el trabajo relacionado: " + e.getMessage());
        }
    }

}

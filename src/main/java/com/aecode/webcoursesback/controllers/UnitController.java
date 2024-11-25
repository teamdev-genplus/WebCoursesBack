package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.SessionDTO;
import com.aecode.webcoursesback.dtos.UnitDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.Session;
import com.aecode.webcoursesback.entities.Unit;
import com.aecode.webcoursesback.services.IModuleService;
import com.aecode.webcoursesback.services.IUnitService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/unit")
public class UnitController {
    @Autowired
    private IUnitService uS;
    @Autowired
    private IModuleService mS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody UnitDTO dto) {
        ModelMapper m = new ModelMapper();
        Unit unit = m.map(dto, Unit.class);
        uS.insert(unit);
        return ResponseEntity.status(201).body("created successfully");
    }

    @GetMapping
    public List<UnitDTO> list() {
        return uS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, UnitDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){uS.delete(id);}

    @GetMapping("/{id}")
    public UnitDTO listId(@PathVariable("id")Integer id){
        ModelMapper m=new ModelMapper();
        UnitDTO dto=m.map(uS.listId(id),UnitDTO.class);
        return dto;
    }
    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Integer id, @RequestBody UnitDTO unitDTO) {
        try {
            // Obtener la unidad existente por ID
            Unit existingUnit = uS.listId(id);
            if (existingUnit == null || existingUnit.getUnitId() == 0) {
                return ResponseEntity.status(404).body("Unidad no encontrada");
            }

            // Actualizar solo los campos proporcionados en el DTO
            if (unitDTO.getTitle() != null) {
                existingUnit.setTitle(unitDTO.getTitle());
            }
            if (unitDTO.getVideoUrl() != null) {
                existingUnit.setVideoUrl(unitDTO.getVideoUrl());
            }
            if (unitDTO.getOrderNumber() != 0) {
                existingUnit.setOrderNumber(unitDTO.getOrderNumber());
            }
            if (unitDTO.getModuleId() != 0) {
                // Relacionar la unidad con otro módulo si el moduleId es diferente
                Module module = mS.listId(unitDTO.getModuleId());
                if (module == null || module.getModuleId() == 0) {
                    return ResponseEntity.status(404).body("Módulo asociado no encontrado");
                }
                existingUnit.setModule(module);
            }

            // Guardar los cambios
            uS.insert(existingUnit);

            return ResponseEntity.ok("Unidad actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar la unidad: " + e.getMessage());
        }
    }


    @GetMapping("/by-course")
    public List<UnitDTO> getUnitsByCourseTitle(@RequestParam("title") String courseTitle) {
        List<Unit> units = uS.findUnitsByCourseTitle(courseTitle);

        if (units.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron unidades para el curso especificado");
        }

        // Convertir la lista de sesiones a SessionDTO
        ModelMapper modelMapper = new ModelMapper();
        return units.stream().map(unidad -> {
            UnitDTO dto = modelMapper.map(unidad, UnitDTO.class);

            return dto;
        }).collect(Collectors.toList());
    }
}

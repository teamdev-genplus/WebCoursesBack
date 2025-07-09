package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.InstructorDTO;
import com.aecode.webcoursesback.entities.Instructor;
import com.aecode.webcoursesback.services.IInstructorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/instructors")
public class InstructorController {
    @Autowired
    private IInstructorService instructorService;

    private ModelMapper modelMapper = new ModelMapper();

    // Crear un nuevo instructor
    @PostMapping
    public ResponseEntity<String> insert(@RequestBody InstructorDTO dto) {
        try {
            Instructor instructor = modelMapper.map(dto, Instructor.class);
            instructorService.insert(instructor);
            return ResponseEntity.status(HttpStatus.CREATED).body("Instructor creado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear instructor: " + e.getMessage());
        }
    }

    // Listar todos los instructores
    @GetMapping
    public List<InstructorDTO> list() {
        List<Instructor> instructors = instructorService.list();
        return instructors.stream()
                .map(instructor -> modelMapper.map(instructor, InstructorDTO.class))
                .collect(Collectors.toList());
    }

    // Obtener instructor por ID
    @GetMapping("/{id}")
    public ResponseEntity<InstructorDTO> listId(@PathVariable Long id) {
        Instructor instructor = instructorService.listId(id);
        if (instructor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        InstructorDTO dto = modelMapper.map(instructor, InstructorDTO.class);
        return ResponseEntity.ok(dto);
    }

    // Actualizar instructor por ID
    @PatchMapping("/{id}")
    public ResponseEntity<String> partialUpdate(@PathVariable Long id, @RequestBody InstructorDTO dto) {
        try {
            Instructor existing = instructorService.listId(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instructor no encontrado");
            }
            if (dto.getName() != null) {
                existing.setName(dto.getName());
            }
            if (dto.getPhotoUrl() != null) {
                existing.setPhotoUrl(dto.getPhotoUrl());
            }
            if (dto.getSpecialties() != null) {
                existing.setSpecialties(dto.getSpecialties());
            }
            instructorService.insert(existing);
            return ResponseEntity.ok("Instructor actualizado parcialmente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar instructor: " + e.getMessage());
        }
    }

    // Eliminar instructor por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            instructorService.delete(id);
            return ResponseEntity.ok("Instructor eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar instructor: " + e.getMessage());
        }
    }
}

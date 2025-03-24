package com.aecode.webcoursesback.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aecode.webcoursesback.dtos.CourseTagDTO;
import com.aecode.webcoursesback.entities.CourseTag;
import com.aecode.webcoursesback.services.ICourseTagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/coursetag")
@RequiredArgsConstructor
public class CourseTagController {

    @Autowired
    private ICourseTagService courseTagService;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody CourseTagDTO dto) {
        System.out.println("Recibido DTO: " + dto);
        System.out.println("DTO courseTagId: " + dto.getCourseTagId());
        System.out.println("DTO courseTagName: " + dto.getCourseTagName());

        if (dto.getCourseTagName() == null || dto.getCourseTagName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: courseTagName no puede ser nulo o vacío.");
        }

        try {
            // Mapear el DTO a la entidad CourseTag
            ModelMapper modelMapper = new ModelMapper();
            CourseTag courseTag = modelMapper.map(dto, CourseTag.class);

            System.out.println("Entidad CourseTag mapeada: " + courseTag);

            // Insertar en la base de datos
            courseTagService.insert(courseTag);

            return ResponseEntity.status(HttpStatus.CREATED).body("Tag creado correctamente");
        } catch (Exception e) {
            System.err.println("Error al crear el tag: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el tag: " + e.getMessage());
        }
    }

    @GetMapping
    public List<CourseTagDTO> list() {
        ModelMapper modelMapper = new ModelMapper();
        List<CourseTag> courseTags = courseTagService.list();
        return courseTags.stream().map(courseTag -> {
            // Mapear la entidad FreqQuest a FreqQuestDTO
            CourseTagDTO dto = modelMapper.map(courseTag, CourseTagDTO.class);
            return dto;
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Integer id) {
        try {
            courseTagService.delete(id);
            return ResponseEntity.ok("Tag eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el tag: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseTagDTO> listId(@PathVariable("id") Integer id) {
        ModelMapper modelMapper = new ModelMapper();
        CourseTag courseTag = courseTagService.listById(id);
        if (courseTag != null) {
            CourseTagDTO dto = modelMapper.map(courseTag, CourseTagDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Integer id, @RequestBody CourseTagDTO dto) {
        try {
            // Obtener la entidad existente por ID
            CourseTag existingCourseTag = courseTagService.listById(id);
            if (existingCourseTag == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag no encontrado");
            }

            if (dto.getCourseTagName() != null) {
                existingCourseTag.setCourseTagName(dto.getCourseTagName());
            }

            // Guardar los cambios
            courseTagService.insert(existingCourseTag);

            return ResponseEntity.ok("Tag actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el tag: " + e.getMessage());
        }
    }
}

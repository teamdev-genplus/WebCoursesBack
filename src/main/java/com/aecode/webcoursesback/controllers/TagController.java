package com.aecode.webcoursesback.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.aecode.webcoursesback.entities.Tag;
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

import com.aecode.webcoursesback.dtos.TagDTO;
import com.aecode.webcoursesback.services.ICourseTagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/coursetag")
@RequiredArgsConstructor
public class TagController {

    @Autowired
    private ICourseTagService courseTagService;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody TagDTO dto) {
        System.out.println("Recibido DTO: " + dto);
        System.out.println("DTO tagId: " + dto.getTagId());
        System.out.println("DTO tagName: " + dto.getTagName());

        if (dto.getTagName() == null || dto.getTagName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: courseTagName no puede ser nulo o vacío.");
        }

        try {
            // Mapear el DTO a la entidad Tag
            ModelMapper modelMapper = new ModelMapper();
            Tag tag = modelMapper.map(dto, Tag.class);

            System.out.println("Entidad Tag mapeada: " + tag);

            // Insertar en la base de datos
            courseTagService.insert(tag);

            return ResponseEntity.status(HttpStatus.CREATED).body("Tag creado correctamente");
        } catch (Exception e) {
            System.err.println("Error al crear el tag: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el tag: " + e.getMessage());
        }
    }

    @GetMapping
    public List<TagDTO> list() {
        ModelMapper modelMapper = new ModelMapper();
        List<Tag> tags = courseTagService.list();
        return tags.stream().map(courseTag -> {
            // Mapear la entidad FreqQuest a FreqQuestDTO
            TagDTO dto = modelMapper.map(courseTag, TagDTO.class);
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
    public ResponseEntity<TagDTO> listId(@PathVariable("id") Integer id) {
        ModelMapper modelMapper = new ModelMapper();
        Tag tag = courseTagService.listById(id);
        if (tag != null) {
            TagDTO dto = modelMapper.map(tag, TagDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Integer id, @RequestBody TagDTO dto) {
        try {
            // Obtener la entidad existente por ID
            Tag existingTag = courseTagService.listById(id);
            if (existingTag == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag no encontrado");
            }

            if (dto.getTagName() != null) {
                existingTag.setName(dto.getTagName());
            }

            // Guardar los cambios
            courseTagService.insert(existingTag);

            return ResponseEntity.ok("Tag actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el tag: " + e.getMessage());
        }
    }
}

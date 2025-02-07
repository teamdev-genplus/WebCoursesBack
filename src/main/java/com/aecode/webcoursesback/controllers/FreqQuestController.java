package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.FreqQuestDTO;
import com.aecode.webcoursesback.entities.FreqQuest;
import com.aecode.webcoursesback.services.IFreqQuestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/freqquest")
public class FreqQuestController {

    @Autowired
    private IFreqQuestService fqS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody FreqQuestDTO dto) {
        try {
            // Mapear el DTO a la entidad FreqQuest
            ModelMapper modelMapper = new ModelMapper();
            FreqQuest freqQuest = modelMapper.map(dto, FreqQuest.class);

            // Insertar el objeto en la base de datos
            fqS.insert(freqQuest);

            return ResponseEntity.status(HttpStatus.CREATED).body("Pregunta frecuente creada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la pregunta frecuente: " + e.getMessage());
        }
    }

    @GetMapping
    public List<FreqQuestDTO> list() {
        ModelMapper modelMapper = new ModelMapper();
        List<FreqQuest> freqQuestions = fqS.list();
        return freqQuestions.stream().map(freqQuest -> {
            // Mapear la entidad FreqQuest a FreqQuestDTO
            FreqQuestDTO dto = modelMapper.map(freqQuest, FreqQuestDTO.class);
            return dto;
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Integer id) {
        try {
            fqS.delete(id);
            return ResponseEntity.ok("Pregunta frecuente eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la pregunta frecuente: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FreqQuestDTO> listId(@PathVariable("id") Integer id) {
        ModelMapper modelMapper = new ModelMapper();
        FreqQuest freqQuest = fqS.listId(id);
        if (freqQuest != null) {
            FreqQuestDTO dto = modelMapper.map(freqQuest, FreqQuestDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Integer id, @RequestBody FreqQuestDTO dto) {
        try {
            // Obtener la entidad existente por ID
            FreqQuest existingFreqQuest = fqS.listId(id);
            if (existingFreqQuest == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pregunta frecuente no encontrada");
            }

            // Actualizar los campos
            if (dto.getQuestionText() != null) {
                existingFreqQuest.setQuestionText(dto.getQuestionText());
            }
            if (dto.getAnswerText() != null) {
                existingFreqQuest.setAnswerText(dto.getAnswerText());
            }

            // Guardar los cambios
            fqS.insert(existingFreqQuest);

            return ResponseEntity.ok("Pregunta frecuente actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la pregunta frecuente: " + e.getMessage());
        }
    }
}
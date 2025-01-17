package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.SessionAnswerDTO;
import com.aecode.webcoursesback.dtos.SessionTestDTO;
import com.aecode.webcoursesback.entities.Session;
import com.aecode.webcoursesback.entities.SessionTest;
import com.aecode.webcoursesback.services.ISessionService;
import com.aecode.webcoursesback.services.ISessionTestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sessiontest")
public class SessionTestController {

    @Autowired
    private ISessionTestService qS;
    @Autowired
    private ISessionService sS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody SessionTestDTO dto) {
        ModelMapper m = new ModelMapper();
        SessionTest s = m.map(dto, SessionTest.class);
        qS.insert(s);
        return ResponseEntity.status(201).body("created successfully");

    }

    @GetMapping
    public List<SessionTestDTO> list() {
        ModelMapper modelMapper = new ModelMapper();

        return qS.list().stream().map(test -> {
            // Convertir el SessionTest a SessionTestDTO
            SessionTestDTO dto = modelMapper.map(test, SessionTestDTO.class);

            // Mapear las respuestas asociadas correctamente
            List<SessionAnswerDTO> mappedAnswers = test.getSessionanswers().stream()
                    .map(answer -> {
                        SessionAnswerDTO answerDTO = modelMapper.map(answer, SessionAnswerDTO.class);
                        answerDTO.setTestId(test.getTestId()); // Asignar el testId manualmente
                        return answerDTO;
                    })
                    .collect(Collectors.toList());

            // Establecer las respuestas mapeadas en el DTO
            dto.setSessionanswers(mappedAnswers);

            return dto;
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        qS.delete(id);
    }

    @GetMapping("/{id}")
    public SessionTestDTO listId(@PathVariable("id") Integer id) {
        ModelMapper m = new ModelMapper();
        SessionTestDTO dto = m.map(qS.listId(id), SessionTestDTO.class);
        return dto;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> update(
            @PathVariable("id") Integer id,
            @RequestBody SessionTestDTO dto) {

        try {
            // Obtener el test existente por ID
            SessionTest existingTest = qS.listId(id);
            if (existingTest == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Test no encontrado");
            }

            // Actualizar campos solo si están presentes en el DTO
            if (dto.getQuestionText() != null) {
                existingTest.setQuestionText(dto.getQuestionText());
            }
            if (dto.getSessionId() != 0) {
                Session session = sS.listId(dto.getSessionId());
                if (session == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sesión asociada no encontrada");
                }
                existingTest.setSession(session);
            }

            // Guardar los cambios en la base de datos
            qS.insert(existingTest);

            return ResponseEntity.ok("Test actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el test: " + e.getMessage());
        }
    }

}

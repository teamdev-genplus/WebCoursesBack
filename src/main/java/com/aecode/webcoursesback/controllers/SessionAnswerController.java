package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.SessionAnswerDTO;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.services.ISessionAnswerService;
import com.aecode.webcoursesback.services.ISessionTestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sessionanswer")
public class SessionAnswerController {
    @Autowired
    private ISessionAnswerService aS;
    @Autowired
    private ISessionTestService stS;

    @PostMapping
    public ResponseEntity<String> insert(@RequestBody SessionAnswerDTO dto) {
        ModelMapper m = new ModelMapper();

        SessionTest stest = stS.listId(dto.getTestId());

        if (stest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test no encontrado");
        }
        // Mapear el DTO a la entidad
        SessionAnswer sanswer = new SessionAnswer();
        sanswer.setSessiontest(stest);
        sanswer.setAnswerText(dto.getAnswerText());
        sanswer.setCorrect(dto.isCorrect());

        // Guardar en la base de datos
        aS.insert(sanswer);

        return ResponseEntity.ok("Respuesta guardado correctamente");
    }

    @GetMapping
    public List<SessionAnswerDTO> list() {
        ModelMapper m = new ModelMapper();
        List<SessionAnswer> a = aS.list();
        return a.stream().map(answer -> {
            SessionAnswerDTO dto = m.map(answer, SessionAnswerDTO.class);
            dto.setTestId(answer.getSessiontest().getTestId());
            return dto;
        }).collect(Collectors.toList());
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id")Integer id){aS.delete(id);}

    @GetMapping("/{id}")
    public SessionAnswerDTO listId(@PathVariable("id")Integer id){
        ModelMapper m = new ModelMapper();
        SessionAnswer answer = aS.listId(id);

        SessionAnswerDTO dto = m.map(answer, SessionAnswerDTO.class);
        dto.setTestId(answer.getSessiontest().getTestId());

        return dto;
    }
    @PatchMapping("/{id}")
    public ResponseEntity<String> update(
            @PathVariable("id") Integer id,
            @RequestBody SessionAnswerDTO dto) {
        try {
            // Obtener la respuesta existente por ID
            SessionAnswer existingAnswer = aS.listId(id);
            if (existingAnswer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Respuesta no encontrada");
            }

            // Actualizar campos solo si están presentes en el DTO
            if (dto.getAnswerText() != null) {
                existingAnswer.setAnswerText(dto.getAnswerText());
            }
            if (dto.isCorrect() != existingAnswer.isCorrect()) {
                existingAnswer.setCorrect(dto.isCorrect());
            }
            if (dto.getTestId() != 0) {
                // Validar si existe el SessionTest asociado
                SessionTest sessionTest = stS.listId(dto.getTestId());
                if (sessionTest == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Test asociado no encontrado");
                }
                existingAnswer.setSessiontest(sessionTest);
            }

            // Guardar los cambios en la base de datos
            aS.insert(existingAnswer);

            return ResponseEntity.ok("Respuesta actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la respuesta: " + e.getMessage());
        }
    }


}

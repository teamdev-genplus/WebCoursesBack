package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.Training.PromotionalCreateUpdateDTO;
import com.aecode.webcoursesback.dtos.Training.PromotionalDTO;
import com.aecode.webcoursesback.services.PromotionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotional")
public class PromotionalController {
    private final PromotionalService promotionalService;

    // Listar todo (admin)
    @GetMapping
    public List<PromotionalDTO> listAll() {
        return promotionalService.listAll();
    }

    // Listar solo activos (público/home)
    @GetMapping("/active")
    public List<PromotionalDTO> listAllActive() {
        return promotionalService.listAllActive();
    }

    // Obtener por id
    @GetMapping("/{id}")
    public PromotionalDTO getById(@PathVariable Long id) {
        return promotionalService.getById(id);
    }

    // Crear
    @PostMapping
    public PromotionalDTO create(@RequestBody PromotionalCreateUpdateDTO dto) {
        return promotionalService.create(dto);
    }

    // Actualizar
    @PutMapping("/{id}")
    public PromotionalDTO update(@PathVariable Long id, @RequestBody PromotionalCreateUpdateDTO dto) {
        return promotionalService.update(id, dto);
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        promotionalService.delete(id);
    }
}

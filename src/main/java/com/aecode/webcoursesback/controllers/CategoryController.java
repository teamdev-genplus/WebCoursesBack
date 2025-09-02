package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.Category.CategoryCreateUpdateDTO;
import com.aecode.webcoursesback.dtos.Category.CategoryDTO;
import com.aecode.webcoursesback.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // ===== Principal: lista para filtros (ordenada por nombre) =====
    @GetMapping
    public List<CategoryDTO> listAll() {
        return categoryService.listAll();
    }

    // ===== Paginado (opcional) =====
    @GetMapping("/paged")
    public Page<CategoryDTO> listPaged(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return categoryService.listPaged(PageRequest.of(page, size));
    }

    // ===== CRUD =====
    @GetMapping("/{id}")
    public CategoryDTO getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PostMapping
    public CategoryDTO create(@RequestBody CategoryCreateUpdateDTO dto) {
        return categoryService.create(dto);
    }

    @PutMapping("/{id}")
    public CategoryDTO update(@PathVariable Long id, @RequestBody CategoryCreateUpdateDTO dto) {
        return categoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}

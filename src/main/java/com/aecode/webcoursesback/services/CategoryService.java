package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.Category.CategoryCreateUpdateDTO;
import com.aecode.webcoursesback.dtos.Category.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    // Listas
    List<CategoryDTO> listAll();                 // principal: para filtros (ordenado por name ASC)
    Page<CategoryDTO> listPaged(Pageable pageable);

    // CRUD
    CategoryDTO getById(Long id);
    CategoryDTO create(CategoryCreateUpdateDTO dto);
    CategoryDTO update(Long id, CategoryCreateUpdateDTO dto);
    void delete(Long id);
}

package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.dtos.Category.CategoryCreateUpdateDTO;
import com.aecode.webcoursesback.dtos.Category.CategoryDTO;
import com.aecode.webcoursesback.entities.Category;
import com.aecode.webcoursesback.repositories.CategoryRepository;
import com.aecode.webcoursesback.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    // ===== Listas =====
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> listAll() {
        List<Category> categories = categoryRepository.findAll(Sort.by("name").ascending());
        return categories.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> listPaged(Pageable pageable) {
        Pageable sorted = withDefaultSort(pageable);
        Page<Category> page = categoryRepository.findAll(sorted);
        return page.map(this::toDTO);
    }

    // ===== CRUD =====
    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getById(Long id) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada"));
        return toDTO(c);
    }

    @Override
    public CategoryDTO create(CategoryCreateUpdateDTO dto) {
        Category c = new Category();
        apply(dto, c);
        try {
            c = categoryRepository.save(c);
        } catch (DataIntegrityViolationException e) {
            // name es unique; dejamos un mensaje claro
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }
        return toDTO(c);
    }

    @Override
    public CategoryDTO update(Long id, CategoryCreateUpdateDTO dto) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada"));
        apply(dto, c);
        try {
            c = categoryRepository.save(c);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }
        return toDTO(c);
    }

    @Override
    public void delete(Long id) {
        // Opcional: validar que no esté referenciada por bots si quieres proteger borrado
        categoryRepository.deleteById(id);
    }

    // ===== Helpers =====
    private Pageable withDefaultSort(Pageable pageable) {
        Sort sort = Sort.by("name").ascending();
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    private CategoryDTO toDTO(Category c) {
        return CategoryDTO.builder()
                .categoryId(c.getCategoryId())
                .name(c.getName())
                .build();
    }

    private void apply(CategoryCreateUpdateDTO dto, Category c) {
        String name = (dto.getName() == null) ? null : dto.getName().trim();
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        // opcional: normalizar mayúsculas/minúsculas según tu criterio
        c.setName(name);
    }
}

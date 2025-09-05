package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.Training.PromotionalCreateUpdateDTO;
import com.aecode.webcoursesback.dtos.Training.PromotionalDTO;

import java.util.List;

public interface PromotionalService {
    List<PromotionalDTO> listAll();
    List<PromotionalDTO> listAllActive();          // solo activos (útil para front público)
    PromotionalDTO getById(Long id);
    PromotionalDTO create(PromotionalCreateUpdateDTO dto);
    PromotionalDTO update(Long id, PromotionalCreateUpdateDTO dto);
    void delete(Long id);

}

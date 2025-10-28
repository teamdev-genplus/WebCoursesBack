package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.dtos.Training.PromotionalCreateUpdateDTO;
import com.aecode.webcoursesback.dtos.Training.PromotionalDTO;
import com.aecode.webcoursesback.entities.Training.Promotional;
import com.aecode.webcoursesback.repositories.Training.PromotionalRepo;
import com.aecode.webcoursesback.services.PromotionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionalServiceImpl implements PromotionalService {
    private final PromotionalRepo promotionalRepo;

    @Override
    @Transactional(readOnly = true)
    public List<PromotionalDTO> listAll() {
        return promotionalRepo.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionalDTO> listAllActive() {
        return promotionalRepo.findByActiveTrueOrderByIdDesc().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionalDTO getById(Long id) {
        Promotional p = promotionalRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Promotional no encontrado"));
        return toDTO(p);
    }

    @Override
    public PromotionalDTO create(PromotionalCreateUpdateDTO dto) {
        Promotional p = new Promotional();
        apply(dto, p);
        p = promotionalRepo.save(p);
        return toDTO(p);
    }

    @Override
    public PromotionalDTO update(Long id, PromotionalCreateUpdateDTO dto) {
        Promotional p = promotionalRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Promotional no encontrado"));
        apply(dto, p);
        p = promotionalRepo.save(p);
        return toDTO(p);
    }

    @Override
    public void delete(Long id) {
        if (!promotionalRepo.existsById(id)) {
            throw new NoSuchElementException("Promotional no encontrado");
        }
        promotionalRepo.deleteById(id);
    }

    // ======= Mappers/Helpers =======

    private PromotionalDTO toDTO(Promotional p) {
        PromotionalDTO dto = new PromotionalDTO();
        dto.setId(p.getId());
        dto.setUrlimage(p.getUrlimage());
        dto.setUrllink(p.getUrllink());
        dto.setActive(p.getActive());
        dto.setText(p.getText());
        return dto;
    }

    private void apply(PromotionalCreateUpdateDTO dto, Promotional p) {
        p.setUrlimage(dto.getUrlimage());
        p.setUrllink(dto.getUrllink());
        p.setActive(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);
        p.setText(dto.getText());
    }

}

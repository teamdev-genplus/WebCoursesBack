package com.aecode.webcoursesback.servicesimplement.Business;
import com.aecode.webcoursesback.dtos.Business.BusinessLogoDto;
import com.aecode.webcoursesback.dtos.Business.CreateBusinessLogoDto;
import com.aecode.webcoursesback.entities.Business.BusinessLogo;
import com.aecode.webcoursesback.repositories.Business.BusinessLogoRepository;
import com.aecode.webcoursesback.services.Business.BusinessLogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessLogoServiceImpl implements BusinessLogoService {

    private final BusinessLogoRepository repository;

    @Override
    public List<BusinessLogoDto> getAll() {
        return repository.findAllByOrderByIdAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public BusinessLogoDto create(CreateBusinessLogoDto request) {
        BusinessLogo entity = BusinessLogo.builder()
                .name(request.getName())
                .logoUrl(request.getLogoUrl())
                .websiteUrl(request.getWebsiteUrl())
                .build();

        BusinessLogo saved = repository.save(entity);
        return toDto(saved);
    }

    private BusinessLogoDto toDto(BusinessLogo e) {
        return BusinessLogoDto.builder()
                .id(e.getId())
                .name(e.getName())
                .logoUrl(e.getLogoUrl())
                .websiteUrl(e.getWebsiteUrl())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
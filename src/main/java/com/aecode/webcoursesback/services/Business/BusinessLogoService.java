package com.aecode.webcoursesback.services.Business;

import com.aecode.webcoursesback.dtos.Business.BusinessLogoDto;
import com.aecode.webcoursesback.dtos.Business.CreateBusinessLogoDto;

import java.util.List;

public interface BusinessLogoService {
    List<BusinessLogoDto> getAll();
    BusinessLogoDto create(CreateBusinessLogoDto request);
}
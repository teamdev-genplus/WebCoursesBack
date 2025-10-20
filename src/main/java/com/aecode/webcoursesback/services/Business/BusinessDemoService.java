package com.aecode.webcoursesback.services.Business;
import com.aecode.webcoursesback.dtos.Business.BusinessDemoRequestDTO;
import com.aecode.webcoursesback.dtos.Business.SubmitBusinessDemoRequestDTO;

public interface BusinessDemoService {
    BusinessDemoRequestDTO submit(SubmitBusinessDemoRequestDTO dto);
}
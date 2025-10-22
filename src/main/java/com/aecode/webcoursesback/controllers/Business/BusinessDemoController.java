package com.aecode.webcoursesback.controllers.Business;

import com.aecode.webcoursesback.dtos.Business.BusinessDemoRequestDTO;
import com.aecode.webcoursesback.dtos.Business.BusinessLogoDto;
import com.aecode.webcoursesback.dtos.Business.CreateBusinessLogoDto;
import com.aecode.webcoursesback.dtos.Business.SubmitBusinessDemoRequestDTO;
import com.aecode.webcoursesback.services.Business.BusinessDemoService;
import com.aecode.webcoursesback.services.Business.BusinessLogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class BusinessDemoController {

    private final BusinessDemoService service;
    private final BusinessLogoService logoservice;

    @PostMapping("/demo-requests")
    public ResponseEntity<BusinessDemoRequestDTO> submit(@Valid @RequestBody SubmitBusinessDemoRequestDTO dto) {
        BusinessDemoRequestDTO out = service.submit(dto);
        return ResponseEntity.ok(out);
    }

    /** GET: listado para el front (logos-marquee) */
    @GetMapping
    public List<BusinessLogoDto> getAll() {
        return logoservice.getAll();
    }

    /** POST: crear logo */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessLogoDto create(@Valid @RequestBody CreateBusinessLogoDto request) {
        return logoservice.create(request);
    }
}

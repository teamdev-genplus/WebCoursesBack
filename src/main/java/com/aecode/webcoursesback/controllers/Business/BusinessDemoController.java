package com.aecode.webcoursesback.controllers.Business;

import com.aecode.webcoursesback.dtos.Business.BusinessDemoRequestDTO;
import com.aecode.webcoursesback.dtos.Business.SubmitBusinessDemoRequestDTO;
import com.aecode.webcoursesback.services.Business.BusinessDemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class BusinessDemoController {

    private final BusinessDemoService service;

    @PostMapping("/demo-requests")
    public ResponseEntity<BusinessDemoRequestDTO> submit(@Valid @RequestBody SubmitBusinessDemoRequestDTO dto) {
        BusinessDemoRequestDTO out = service.submit(dto);
        return ResponseEntity.ok(out);
    }
}

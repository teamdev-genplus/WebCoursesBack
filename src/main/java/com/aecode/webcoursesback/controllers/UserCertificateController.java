package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.Certificate.CertificateDetailDTO;
import com.aecode.webcoursesback.dtos.Certificate.CertificateVerifyRequestDTO;
import com.aecode.webcoursesback.dtos.Certificate.CertificateVerifyResponseDTO;
import com.aecode.webcoursesback.dtos.Certificate.UserCertificateDTO;
import com.aecode.webcoursesback.services.ICertificateLookupService;
import com.aecode.webcoursesback.services.IUserCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-certificates")
@RequiredArgsConstructor
public class UserCertificateController {
    @Autowired
    private IUserCertificateService userCertificateService;
    private final ICertificateLookupService lookupService;

    //---------------------------------------------------------VALIDACION DE CERTIFICADO------------------------------------------------
    /**
     * Paso 1: Validar por código
     * Body: { "code": "AEC-2025-0001" }
     */
    @PostMapping("/verify")
    public ResponseEntity<CertificateVerifyResponseDTO> verify(@RequestBody CertificateVerifyRequestDTO req) {
        CertificateVerifyResponseDTO res = lookupService.verify(req.getCode());
        return ResponseEntity.ok(res);
    }

    /**
     * Paso 2: Obtener detalle para el overlay (si verified==true)
     */
    @GetMapping("/{code}")
    public ResponseEntity<CertificateDetailDTO> getDetails(@PathVariable String code) {
        CertificateDetailDTO dto = lookupService.getDetails(code);
        return ResponseEntity.ok(dto);
    }

    //----------------------------------------------------------GET----------------------------------------------------------------
    // Obtener certificados de un usuario
    @GetMapping("/user/{clerkId}")
    public ResponseEntity<List<UserCertificateDTO>> getCertificatesByUser(@PathVariable String clerkId) {
        List<UserCertificateDTO> certificates = userCertificateService.getCertificatesByUser(clerkId);
        return ResponseEntity.ok(certificates);
    }

    //----------------------------------------------------------POST----------------------------------------------------------------
    // Agregar un certificado a un usuario
    @PostMapping
    public ResponseEntity<UserCertificateDTO> addUserCertificate(@RequestBody UserCertificateDTO dto) {
        UserCertificateDTO saved = userCertificateService.addUserCertificate(dto);
        return ResponseEntity.ok(saved);
    }

    //----------------------------------------------------------DELETE----------------------------------------------------------------
    // Eliminar un certificado por id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserCertificate(@PathVariable Long id) {
        userCertificateService.deleteUserCertificate(id);
        return ResponseEntity.noContent().build();
    }
}

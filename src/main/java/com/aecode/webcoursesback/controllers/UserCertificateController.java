package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserCertificateDTO;
import com.aecode.webcoursesback.services.IUserCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-certificates")
public class UserCertificateController {
    @Autowired
    private IUserCertificateService userCertificateService;

    //----------------------------------------------------------GET----------------------------------------------------------------
    // Obtener certificados de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserCertificateDTO>> getCertificatesByUser(@PathVariable Long userId) {
        List<UserCertificateDTO> certificates = userCertificateService.getCertificatesByUser(userId);
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

package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentValidateRequest;
import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentVoucherDTO;
import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentVoucherPayload;
import com.aecode.webcoursesback.services.Paid.Voucher.ManualPaymentVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/manual-payments/vouchers")
@RequiredArgsConstructor
public class ManualPaymentVoucherController {

    private final ManualPaymentVoucherService service;

    /**
     * Sube voucher + metadatos en 2 partes:
     *  - file (opcional): PDF/JPG/PNG
     *  - payload (obligatorio): JSON con los campos (clerkId, domain, orderId, etc.)
     *
     * Content-Type: multipart/form-data
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ManualPaymentVoucherDTO> uploadVoucher(
            @RequestPart(name = "file", required = false) MultipartFile file,
            @RequestPart(name = "payload", required = true) ManualPaymentVoucherPayload payload
    ) {
        ManualPaymentVoucherDTO dto = service.uploadVoucher(file, payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Cambia 'validated'. Si pasa a true:
     *  - EVENT => no exporta módulos (solo marca)
     *  - MODULES/null => exporta a unificada módulos válidos
     */
    @PatchMapping("/{id}/validate")
    public ResponseEntity<ManualPaymentVoucherDTO> setValidated(
            @PathVariable Long id,
            @RequestBody ManualPaymentValidateRequest body
    ) {
        boolean validated = body != null && Boolean.TRUE.equals(body.getValidated());
        ManualPaymentVoucherDTO dto = service.setValidated(id, validated);
        return ResponseEntity.ok(dto);
    }

    /** Admin: lista todas las cargas de vouchers */
    @GetMapping
    public List<ManualPaymentVoucherDTO> listAll() {
        return service.listAll();
    }
}

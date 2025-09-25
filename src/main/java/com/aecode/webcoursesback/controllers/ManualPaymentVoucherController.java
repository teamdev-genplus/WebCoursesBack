package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentValidateRequest;
import com.aecode.webcoursesback.dtos.Paid.Voucher.ManualPaymentVoucherDTO;
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
     * Cargar voucher + datos. Ningún campo es obligatorio.
     * Form-data (multipart):
     *  - file: (opcional) pdf/jpg/png
     *  - clerkId: (opcional)
     *  - moduleIds: (opcional) puede venir repetido => &moduleIds=1&moduleIds=2...
     *  - paymentMethod: (opcional) texto libre
     *  - paidAt: (opcional) ISO-8601 p.ej. 2025-09-23T12:34:56Z
     *  - status: (opcional) "PENDING" | "PAID" (default PAID)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ManualPaymentVoucherDTO> uploadVoucher(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam(name = "clerkId", required = false) String clerkId,
            @RequestParam(name = "moduleIds", required = false) List<Long> moduleIds,
            @RequestParam(name = "paymentMethod", required = false) String paymentMethod,
            @RequestParam(name = "paidAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime paidAt,
            @RequestParam(name = "status", required = false) String status
    ) {
        ManualPaymentVoucherDTO dto = service.uploadVoucher(file, clerkId, moduleIds, paymentMethod, paidAt, status);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Actualizar validación. Si validated=true:
     *  - Verifica clerkId existe
     *  - Verifica módulos existen y NO repetidos en UserModuleAccess
     *  - Inserta en UnifiedPaidOrder
     */
    @PatchMapping("/{id}/validate")
    public ResponseEntity<?> setValidated(
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

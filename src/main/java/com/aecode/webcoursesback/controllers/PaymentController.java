package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.Izipay.*;
import com.aecode.webcoursesback.services.Izipay.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    // Paso 1/2/3: Crear formToken y devolver también publicKey al front
    @PostMapping("/formtoken")
    public FormTokenCreateResponse createFormToken(@RequestBody FormTokenCreateRequest req) {
        return paymentService.createFormToken(req);
    }

    // Paso 4: Validar retorno del navegador (front -> back)
    @PostMapping("/validate")
    public ValidatePaymentResponse validate(@RequestBody ValidatePaymentRequest req) {
        return paymentService.validateBrowserReturn(req);
    }

    // Paso 5: IPN (Izipay -> back). Contenido: x-www-form-urlencoded
    @PostMapping(value = "/ipn", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String ipn(@RequestParam Map<String, String> formParams) {
        return paymentService.handleIpn(formParams);
    }


    // ==== NUEVO: Listado para Excel - dominio MODULES ====
    @GetMapping("/orders/modules")
    public List<PaymentOrderExportDTO> listModuleOrders() {
        return paymentService.listModuleOrders();
    }

    // ==== NUEVO: Listado para Excel - dominio EVENT ====
    @GetMapping("/orders/events")
    public List<PaymentOrderExportDTO> listEventOrders() {
        return paymentService.listEventOrders();
    }

}

package com.aecode.webcoursesback.services.Izipay;
import com.aecode.webcoursesback.dtos.Izipay.*;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    FormTokenCreateResponse createFormToken(FormTokenCreateRequest req);
    ValidatePaymentResponse validateBrowserReturn(ValidatePaymentRequest req);
    String handleIpn(Map<String, String> formParams); // devuelve "OK" si todo bien


    // NUEVOS: listados por dominio
    List<PaymentOrderExportDTO> listModuleOrders();
    List<PaymentOrderExportDTO> listEventOrders();
}

package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.Izipay.FormTokenCreateRequest;
import com.aecode.webcoursesback.dtos.Izipay.FormTokenCreateResponse;
import com.aecode.webcoursesback.dtos.Izipay.ValidatePaymentRequest;
import com.aecode.webcoursesback.dtos.Izipay.ValidatePaymentResponse;

import java.util.Map;

public interface PaymentService {
    FormTokenCreateResponse createFormToken(FormTokenCreateRequest req);
    ValidatePaymentResponse validateBrowserReturn(ValidatePaymentRequest req);
    String handleIpn(Map<String, String> formParams); // devuelve "OK" si todo bien
}

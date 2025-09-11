package com.aecode.webcoursesback.services.Izipay;

import com.aecode.webcoursesback.entities.Izipay.PaymentOrder;
public interface PaymentEntitlementService {
    void fulfillIfPaid(PaymentOrder order);
}

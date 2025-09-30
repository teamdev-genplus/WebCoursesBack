package com.aecode.webcoursesback.services.Izipay;

import com.aecode.webcoursesback.entities.Izipay.PaymentOrder;

import java.util.List;

public interface PaymentEntitlementService {
    GrantResult fulfillIfPaid(PaymentOrder order);

    @lombok.Value
    class GrantResult {
        List<Long> grantedModuleIds;
        List<Long> skippedModuleIds;
    }
}

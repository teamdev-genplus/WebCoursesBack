package com.aecode.webcoursesback.services.Paid;

import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseRequestDTO;
import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseResponseDTO;

public interface PurchaseAccessService {
    AccessPurchaseResponseDTO processFrontAssertedPurchase(AccessPurchaseRequestDTO req);
}

package com.aecode.webcoursesback.services.Paid;

import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseRequestDTO;
import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseResponseDTO;
import com.aecode.webcoursesback.dtos.Paid.Event.AccessEventPurchaseRequestDTO;
import com.aecode.webcoursesback.dtos.Paid.Event.AccessEventPurchaseResponseDTO;

public interface PurchaseAccessService {
    AccessPurchaseResponseDTO processFrontAssertedPurchase(AccessPurchaseRequestDTO req);
    // NUEVO: EVENT (Landing)
    AccessEventPurchaseResponseDTO processFrontAssertedEventPurchase(String slug, AccessEventPurchaseRequestDTO req);
}
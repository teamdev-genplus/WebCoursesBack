package com.aecode.webcoursesback.services.Paid;

import com.aecode.webcoursesback.dtos.Paid.UnifiedPaidOrderDTO;

import java.time.OffsetDateTime;
import java.util.List;

public interface UnifiedPaidOrderService {
    UnifiedPaidOrderDTO logPaidOrder(String email, String fullName, OffsetDateTime paidAt, List<Long> moduleIds);
    List<UnifiedPaidOrderDTO> getAll();
}
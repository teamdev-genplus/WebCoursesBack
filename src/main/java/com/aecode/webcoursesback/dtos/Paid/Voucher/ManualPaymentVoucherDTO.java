package com.aecode.webcoursesback.dtos.Paid.Voucher;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ManualPaymentVoucherDTO {
    private Long id;
    private String clerkId;
    private String voucherUrl;
    private List<Long> moduleIds; // parseado desde CSV
    private String paymentMethod;
    private String status;        // "PENDING" | "PAID"
    private OffsetDateTime paidAt;
    private boolean validated;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
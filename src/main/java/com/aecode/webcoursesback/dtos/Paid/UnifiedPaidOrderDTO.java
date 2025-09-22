package com.aecode.webcoursesback.dtos.Paid;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UnifiedPaidOrderDTO {
    private Long id;
    private String email;
    private String fullName;
    private String status;              // "PAID"
    private OffsetDateTime paidAt;
    private List<Long> moduleIds;       // parseado de CSV
    private List<String> moduleTitles;  // enriquecido para la vista admin
}

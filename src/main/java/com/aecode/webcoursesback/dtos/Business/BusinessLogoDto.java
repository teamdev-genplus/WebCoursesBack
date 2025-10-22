package com.aecode.webcoursesback.dtos.Business;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusinessLogoDto {
    private Long id;
    private String name;
    private String logoUrl;
    private String websiteUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
package com.aecode.webcoursesback.dtos;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class moduleBenefitsDTO {
    private Long id;
    private Long moduleId;
    private String benefits;

}
package com.aecode.webcoursesback.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleCartDTO {
    private Long moduleId;
    private String programTitle;
    private Double priceRegular;
    private Double promptPaymentPrice;
    private boolean selected;
}

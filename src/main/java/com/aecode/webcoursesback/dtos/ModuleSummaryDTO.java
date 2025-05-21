package com.aecode.webcoursesback.dtos;
import com.aecode.webcoursesback.entities.Module;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleSummaryDTO {
    private int moduleId;
    private String courseTitle;  // Desde Course
    private String programTitle;
    private String description;
    private String brochureUrl;
    private LocalDate startDate;
    private Double priceRegular;
    private Boolean isOnSale;
    private String principalImage;
    private Integer orderNumber;
    private Module.Mode mode;
    private String certificateHours;
    private Double discountPercentage;
    private Double promptPaymentPrice;
    private String urlName;
    private String type;
}
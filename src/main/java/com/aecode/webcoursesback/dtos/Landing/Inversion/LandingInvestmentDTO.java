package com.aecode.webcoursesback.dtos.Landing.Inversion;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandingInvestmentDTO {
    private List<PlanTitleDTO> plans;           // Tipo de tarifa (títulos)
    private SelectedPlanBenefitsDTO selected;   // Beneficios + totales del plan elegido
}

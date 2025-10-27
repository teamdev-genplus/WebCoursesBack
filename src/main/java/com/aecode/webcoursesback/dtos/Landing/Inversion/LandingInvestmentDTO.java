package com.aecode.webcoursesback.dtos.Landing.Inversion;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandingInvestmentDTO {
    /** Modalidades detectadas en la landing (ej: ["PRESENCIAL","VIRTUAL"]) */
    private List<ModalityTitleDTO> modalities;

    /** Modalidad actualmente usada para filtrar planes (null si no hay) */
    private String activeModality;

    private List<PlanTitleDTO> plans;           // Tipo de tarifa (títulos)
    private SelectedPlanBenefitsDTO selected;   // Beneficios + totales del plan elegido
}

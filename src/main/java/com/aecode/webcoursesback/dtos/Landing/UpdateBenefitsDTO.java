package com.aecode.webcoursesback.dtos.Landing;
import com.aecode.webcoursesback.entities.Landing.LandingPage.*;
import lombok.*;
import java.util.List;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateBenefitsDTO {
    private List<Benefit> benefits;
}
package com.aecode.webcoursesback.dtos.Landing;

import com.aecode.webcoursesback.entities.Landing.LandingPage;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateCallForPresentationDTO {
    private LandingPage.CallForPresentationSection callForPresentation;
}

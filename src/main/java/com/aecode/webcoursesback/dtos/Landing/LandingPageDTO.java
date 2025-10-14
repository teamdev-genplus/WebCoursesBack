package com.aecode.webcoursesback.dtos.Landing;
import com.aecode.webcoursesback.entities.Landing.LandingPage.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LandingPageDTO {
    private Long id;
    private String slug;

    private List<PrincipalSection> principal;
    private List<Collaborator> collaborators;
    private AboutSection about;
    private List<Speaker> speakers;
    /** NUEVO */
    private CallForPresentationSection callForPresentation;

    private List<Benefit> benefits;
    private List<PricingPlan> pricing;
    private SocialSection social;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

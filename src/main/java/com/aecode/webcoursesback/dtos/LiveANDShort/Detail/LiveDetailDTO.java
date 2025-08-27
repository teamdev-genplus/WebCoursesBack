package com.aecode.webcoursesback.dtos.LiveANDShort.Detail;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class LiveDetailDTO {
    private Long id;
    private String title;

    // misma imagen que el destacado
    private String featuredImageUrl;

    private String longDescription;

    private LocalDateTime startDateTime; // para formatear "20 de Marzo", "7:00 pm"
    private Integer durationMinutes;     // visible si ya pasó

    // Acción principal según estado
    private String primaryActionLabel;   // "Inscribirse" | "Ver ahora" | "NO DISPONIBLE POR EL MOMENTO"
    private String primaryActionUrl;     // registrationUrl o playbackUrl (o null)

    private List<InstructorBriefDTO> instructors;

    // Recomendaciones
    private List<ShortRecommendationDTO> relatedShorts;
    private List<LiveRecommendationDTO> relatedLives;
}

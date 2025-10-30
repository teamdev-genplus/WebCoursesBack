package com.aecode.webcoursesback.dtos.Landing.Participantes;

import com.aecode.webcoursesback.dtos.Landing.Inversion.ParticipantDTO;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParticipantGroupDTO {
    private String groupId;
    private String landingSlug;      // para identificar la landing del grupo
    private String modality;         // si todos coinciden; si no, "MIXED"
    private String planKey;          // si todos coinciden; si no, "MIXED"
    private Integer quantity;        // total de participantes del grupo
    private String statusAggregated; // PENDING | CONFIRMED | MIXED
    private OffsetDateTime createdAtMin; // rango de fechas (útil para ordenar)
    private OffsetDateTime createdAtMax;
    private OffsetDateTime updatedAtMax;

    private List<ParticipantDTO> participants; // ya lo tienes
}

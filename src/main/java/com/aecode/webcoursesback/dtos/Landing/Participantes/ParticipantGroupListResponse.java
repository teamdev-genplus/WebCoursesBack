package com.aecode.webcoursesback.dtos.Landing.Participantes;
import com.aecode.webcoursesback.dtos.Landing.ParticipantGroupDTO;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParticipantGroupListResponse {
    private String buyerClerkId;
    private String slug;     // filtro opcional usado
    private String status;   // ALL | PENDING | CONFIRMED usado
    private List<ParticipantGroupDTO> groups;
}
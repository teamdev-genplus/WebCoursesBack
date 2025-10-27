package com.aecode.webcoursesback.dtos.Landing.Inversion;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParticipantListResponse {
    private String groupId;
    private List<ParticipantDTO> participants;
}

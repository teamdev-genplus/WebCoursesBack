package com.aecode.webcoursesback.dtos.support.admin;
import lombok.*;

import java.util.List;

/** PATCH videos (remplaza la lista completa ordenada) */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateVideosDTO {
    private List<SupportGuideUpsertDTO.VideoAdmin> videos;
}
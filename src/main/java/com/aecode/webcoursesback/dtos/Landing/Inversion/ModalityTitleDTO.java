package com.aecode.webcoursesback.dtos.Landing.Inversion;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModalityTitleDTO {
    private String key;    // "PRESENCIAL" | "VIRTUAL"
    private String title;  // Texto para UI (puedes dejar igual que key o traducir)
}

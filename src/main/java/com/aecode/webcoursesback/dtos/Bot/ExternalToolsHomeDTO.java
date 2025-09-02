package com.aecode.webcoursesback.dtos.Bot;
import lombok.*;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ExternalToolsHomeDTO {
    private ExternalToolCardDTO highlighted;
    private List<ExternalToolCardDTO> others; // tamaño máx 6
}

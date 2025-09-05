package com.aecode.webcoursesback.dtos.Training;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class PromotionalDTO {
    private Long id;
    private String urlimage;
    private String urllink;
    private Boolean active;
}

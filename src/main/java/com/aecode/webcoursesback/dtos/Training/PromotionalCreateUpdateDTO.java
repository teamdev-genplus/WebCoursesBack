package com.aecode.webcoursesback.dtos.Training;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class PromotionalCreateUpdateDTO {
    private String urlimage;
    private String urllink;
    private Boolean active;
    private String text;
}
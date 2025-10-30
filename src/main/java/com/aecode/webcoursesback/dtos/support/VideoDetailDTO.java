package com.aecode.webcoursesback.dtos.support;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VideoDetailDTO {
    private String key;
    private String title;
    private String description;
    private String videoUrl;
    private String durationLabel;
    private List<FaqDTO> faqs;
}
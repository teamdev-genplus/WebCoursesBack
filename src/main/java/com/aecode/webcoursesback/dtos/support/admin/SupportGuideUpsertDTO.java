package com.aecode.webcoursesback.dtos.support.admin;

import lombok.*;
import java.util.List;

/** POST /support/admin — crea o reemplaza página */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupportGuideUpsertDTO {
    private String slug;
    private String title;
    private String intro;
    private List<VideoAdmin> videos;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VideoAdmin {
        private String key;
        private Integer position;
        private String title;
        private String description;
        private String thumbnailUrl;
        private String videoUrl;
        private String durationLabel;
        private List<FaqAdmin> faqs;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FaqAdmin {
        private String question;
        private String answer;
    }
}

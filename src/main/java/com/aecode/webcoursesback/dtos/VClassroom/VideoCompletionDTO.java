package com.aecode.webcoursesback.dtos.VClassroom;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VideoCompletionDTO {
    private Long videoId;
    private String clerkId;
    private boolean completed;
}

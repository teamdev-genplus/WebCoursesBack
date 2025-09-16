package com.aecode.webcoursesback.entities.VClassroom;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MarkVideoCompletedRequest {
    private String clerkId;
    private Boolean completed; // default true si viene null
}
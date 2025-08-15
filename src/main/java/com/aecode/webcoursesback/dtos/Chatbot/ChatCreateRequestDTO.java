package com.aecode.webcoursesback.dtos.Chatbot;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatCreateRequestDTO {
    private String clerkId;
    private String chatKey;          // viene del front
    private String displayName;      // opcional; si es null, genera "Chat N"
}

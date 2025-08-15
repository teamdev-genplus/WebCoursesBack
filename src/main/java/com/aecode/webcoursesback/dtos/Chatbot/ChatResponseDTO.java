package com.aecode.webcoursesback.dtos.Chatbot;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatResponseDTO {
    private Long chatId;
    private String displayName;
    private String chatKey;
    private LocalDateTime createdAt;
}

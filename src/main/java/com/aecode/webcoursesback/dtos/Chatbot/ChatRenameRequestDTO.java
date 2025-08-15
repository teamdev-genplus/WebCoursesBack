package com.aecode.webcoursesback.dtos.Chatbot;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatRenameRequestDTO {
    private String displayName;
}

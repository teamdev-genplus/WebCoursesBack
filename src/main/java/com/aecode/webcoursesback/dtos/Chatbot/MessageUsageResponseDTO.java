package com.aecode.webcoursesback.dtos.Chatbot;
import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageUsageResponseDTO {
    private boolean allowed;        // puede enviar mensaje?
    private int remaining;          // mensajes restantes hoy
    private LocalDate resetsAt;     // fecha (00:00 del día siguiente)
    private String message;         // texto amigable
}

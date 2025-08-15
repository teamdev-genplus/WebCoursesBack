package com.aecode.webcoursesback.entities.Chatbot;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "chat_sessions",
        indexes = {
                @Index(name = "idx_chat_sessions_clerk", columnList = "clerk_id")
        }
)
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Si tienes relación con UserProfile y quieres FK fuerte,
    // puedes reemplazar por ManyToOne a UserProfile y usar join.
    @Column(name = "clerk_id", nullable = false)
    private String clerkId;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName; // nombre del chat (editable)

    @Column(name = "chat_key_encrypted", nullable = false, columnDefinition = "TEXT")
    private String chatKeyEncrypted;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (active == null) active = true;
    }
}

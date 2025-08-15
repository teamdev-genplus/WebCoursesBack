package com.aecode.webcoursesback.entities.Chatbot;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "user_daily_chat_usage",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usage_clerk_date", columnNames = {"clerk_id", "usage_date"})
        },
        indexes = {
                @Index(name = "idx_usage_clerk_date", columnList = "clerk_id,usage_date")
        }
)
public class UserDailyChatUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clerk_id", nullable = false)
    private String clerkId;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "message_count", nullable = false)
    private Integer messageCount;

    @PrePersist
    public void prePersist() {
        if (messageCount == null) messageCount = 0;
    }
}

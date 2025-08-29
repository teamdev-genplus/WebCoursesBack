package com.aecode.webcoursesback.entities.Bot;

import com.aecode.webcoursesback.entities.UserProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "user_bot_access",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "bot_id"})
)
@SequenceGenerator(name = "bot_access_seq", sequenceName = "bot_access_sequence", allocationSize = 1)

public class UserBotAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bot_access_seq")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Column(nullable = false)
    private boolean hasAccess; // true si lo adquirió (FREE/PAID)

    @Column(nullable = false)
    private LocalDateTime grantedAt;

    // Fuente de acceso (FREE/PURCHASE/ADMIN), útil para auditoría
    @Column(length = 20)
    private String source;
}

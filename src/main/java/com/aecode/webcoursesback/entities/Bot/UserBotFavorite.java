package com.aecode.webcoursesback.entities.Bot;
import com.aecode.webcoursesback.entities.UserProfile;
import jakarta.persistence.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "user_bot_favorites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "bot_id"})
})
public class UserBotFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteId;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne @JoinColumn(name = "bot_id", nullable = false)
    private Bot bot;
}

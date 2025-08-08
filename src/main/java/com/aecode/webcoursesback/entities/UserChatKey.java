package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_chat_key")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChatKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clerk_id", unique = true, nullable = false)
    private String clerkId;

    @Column(name = "chat_key", nullable = false)
    private String chatKey;
}

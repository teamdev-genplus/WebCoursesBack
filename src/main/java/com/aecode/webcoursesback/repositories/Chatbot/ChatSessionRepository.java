package com.aecode.webcoursesback.repositories.Chatbot;

import com.aecode.webcoursesback.entities.Chatbot.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByClerkIdOrderByCreatedAtDesc(String clerkId);
    int countByClerkId(String clerkId);
    Optional<ChatSession> findByIdAndClerkId(Long id, String clerkId);
}

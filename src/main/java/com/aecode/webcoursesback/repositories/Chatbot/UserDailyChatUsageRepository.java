package com.aecode.webcoursesback.repositories.Chatbot;

import com.aecode.webcoursesback.entities.Chatbot.UserDailyChatUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserDailyChatUsageRepository extends JpaRepository<UserDailyChatUsage, Long> {
    Optional<UserDailyChatUsage> findByClerkIdAndUsageDate(String clerkId, LocalDate usageDate);
}

package com.aecode.webcoursesback.repositories;
import com.aecode.webcoursesback.entities.UserChatKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserChatKeyRepository extends JpaRepository<UserChatKey, Long> {
    Optional<UserChatKey> findByClerkId(String clerkId);
    void deleteByClerkId(String clerkId);
    boolean existsByClerkId(String clerkId);
}

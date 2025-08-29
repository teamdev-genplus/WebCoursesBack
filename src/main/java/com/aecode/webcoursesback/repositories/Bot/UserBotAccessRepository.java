package com.aecode.webcoursesback.repositories.Bot;

import com.aecode.webcoursesback.entities.Bot.Bot;
import com.aecode.webcoursesback.entities.Bot.UserBotAccess;
import com.aecode.webcoursesback.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBotAccessRepository extends JpaRepository<UserBotAccess, Long> {
    boolean existsByUserProfileAndBot(UserProfile userProfile, Bot bot);
    Optional<UserBotAccess> findByUserProfileAndBot(UserProfile userProfile, Bot bot);
    List<UserBotAccess> findByUserProfile(UserProfile userProfile);
}

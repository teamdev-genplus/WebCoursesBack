package com.aecode.webcoursesback.repositories.Bot;
import com.aecode.webcoursesback.entities.Bot.Bot;
import com.aecode.webcoursesback.entities.Bot.UserBotFavorite;
import com.aecode.webcoursesback.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
    public interface UserBotFavoriteRepository extends JpaRepository<UserBotFavorite, Long> {
        @Query("select f.bot.botId from UserBotFavorite f " +
                "where f.userProfile = :user and f.bot.botId in :botIds")
        List<Long> findFavoriteBotIdsByUserAndBotIdIn(
                @Param("user") UserProfile user,
                @Param("botIds") Collection<Long> botIds
        );

        boolean existsByUserProfileAndBot(UserProfile userProfile, Bot bot);
        Optional<UserBotFavorite> findByUserProfileAndBot(UserProfile userProfile, Bot bot);
        List<UserBotFavorite> findByUserProfile(UserProfile userProfile);
    }
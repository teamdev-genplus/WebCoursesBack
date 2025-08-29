package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.dtos.Bot.AccessStatusDTO;
import com.aecode.webcoursesback.dtos.Bot.GrantAccessRequestDTO;
import com.aecode.webcoursesback.entities.Bot.Bot;
import com.aecode.webcoursesback.entities.Bot.UserBotAccess;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.Bot.BotRepository;
import com.aecode.webcoursesback.repositories.Bot.UserBotAccessRepository;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.BotAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BotAccessServiceImpl implements BotAccessService {
    private final IUserProfileRepository userProfileRepository;
    private final BotRepository botRepository;
    private final UserBotAccessRepository userBotAccessRepository;

    @Override
    public AccessStatusDTO grantAccess(GrantAccessRequestDTO request) {
        UserProfile user = userProfileRepository.findByClerkId(request.getClerkId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado por clerkId"));

        Bot bot = botRepository.findById(request.getBotId())
                .orElseThrow(() -> new NoSuchElementException("Bot no encontrado"));

        if (bot.getType() != Bot.BotType.INTERNAL) {
            throw new IllegalStateException("Solo los AECObots (INTERNAL) manejan accesos");
        }

        UserBotAccess access = userBotAccessRepository
                .findByUserProfileAndBot(user, bot)
                .orElseGet(() -> UserBotAccess.builder()
                        .userProfile(user)
                        .bot(bot)
                        .build()
                );

        access.setHasAccess(true);
        access.setGrantedAt(LocalDateTime.now());
        access.setSource(request.getSource() != null ? request.getSource() : "FREE");

        userBotAccessRepository.save(access);

        return new AccessStatusDTO(bot.getBotId(), true);
    }

    @Override
    public AccessStatusDTO revokeAccess(String clerkId, Long botId) {
        UserProfile user = userProfileRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado por clerkId"));

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new NoSuchElementException("Bot no encontrado"));

        userBotAccessRepository.findByUserProfileAndBot(user, bot).ifPresent(userBotAccessRepository::delete);

        return new AccessStatusDTO(bot.getBotId(), false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccessStatusDTO> listMyAecobots(String clerkId) {
        UserProfile user = userProfileRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado por clerkId"));

        return userBotAccessRepository.findByUserProfile(user).stream()
                .map(a -> new AccessStatusDTO(a.getBot().getBotId(), a.isHasAccess()))
                .toList();
    }
}

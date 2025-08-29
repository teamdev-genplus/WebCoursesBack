package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.dtos.Bot.AecobotCardDTO;
import com.aecode.webcoursesback.dtos.Bot.BotCreateUpdateDTO;
import com.aecode.webcoursesback.dtos.Bot.BotLinkDTO;
import com.aecode.webcoursesback.dtos.Bot.ExternalToolCardDTO;
import com.aecode.webcoursesback.entities.Bot.Bot;
import com.aecode.webcoursesback.entities.Tag;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.Bot.BotRepository;
import com.aecode.webcoursesback.repositories.Bot.UserBotAccessRepository;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.repositories.TagRepository;
import com.aecode.webcoursesback.services.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BotServiceImpl implements BotService {
    private final BotRepository botRepository;
    private final UserBotAccessRepository userBotAccessRepository;
    private final IUserProfileRepository userProfileRepository;
    private final TagRepository tagRepository;

    // --- Listas para UI ---

    @Override
    @Transactional(readOnly = true)
    public List<AecobotCardDTO> listAecobotsForHome(String clerkId) {
        List<Bot> bots = botRepository.findByTypeAndActiveTrueOrderByTitleAsc(Bot.BotType.INTERNAL);
        UserProfile user = loadUserByClerkIdOrNull(clerkId);

        return bots.stream()
                .map(b -> toAecobotCardDTO(b, user))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExternalToolCardDTO> listExternalToolsForHome() {
        List<Bot> bots = botRepository.findByTypeAndActiveTrueOrderByTitleAsc(Bot.BotType.EXTERNAL);
        return bots.stream().map(this::toExternalToolCardDTO).toList();
    }

    // --- CRUD Admin ---

    @Override
    public AecobotCardDTO createBot(BotCreateUpdateDTO dto) {
        Bot bot = new Bot();
        apply(dto, bot);
        bot = botRepository.save(bot);
        // En retorno usamos forma AecobotCardDTO si es INTERNAL; si EXTERNAL mapeamos a campos compatibles
        return toAecobotCardDTO(bot, null);
    }

    @Override
    public AecobotCardDTO updateBot(Long botId, BotCreateUpdateDTO dto) {
        Bot bot = botRepository.findById(botId).orElseThrow(() -> new NoSuchElementException("Bot no encontrado"));
        apply(dto, bot);
        bot = botRepository.save(bot);
        return toAecobotCardDTO(bot, null);
    }

    @Override
    public void deleteBot(Long botId) {
        botRepository.deleteById(botId);
    }

    @Override
    @Transactional(readOnly = true)
    public BotLinkDTO getBotLink(Long botId) {
        Bot bot = botRepository.findById(botId).orElseThrow(() -> new NoSuchElementException("Bot no encontrado"));
        return new BotLinkDTO(bot.getBotId(), bot.getRedirectUrl());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AecobotCardDTO> listAecobotsPaged(String clerkId, Pageable pageable) {
        Page<Bot> page = botRepository.findByTypeAndActiveTrue(Bot.BotType.INTERNAL, pageable);
        UserProfile user = loadUserByClerkIdOrNull(clerkId);
        List<AecobotCardDTO> content = page.getContent().stream()
                .map(b -> toAecobotCardDTO(b, user))
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExternalToolCardDTO> listExternalToolsPaged(Pageable pageable) {
        Page<Bot> page = botRepository.findByTypeAndActiveTrue(Bot.BotType.EXTERNAL, pageable);
        List<ExternalToolCardDTO> content = page.getContent().stream()
                .map(this::toExternalToolCardDTO)
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    // --- Helpers ---

    private UserProfile loadUserByClerkIdOrNull(String clerkId) {
        if (clerkId == null || clerkId.isBlank()) return null;
        return userProfileRepository.findByClerkId(clerkId).orElse(null);
    }

    private void apply(BotCreateUpdateDTO dto, Bot bot) {
        bot.setType(dto.getType());
        bot.setTitle(dto.getTitle());
        bot.setOwnerName(dto.getOwnerName());
        bot.setImageUrl(dto.getImageUrl());
        bot.setShortDescription(dto.getShortDescription());
        bot.setRedirectUrl(dto.getRedirectUrl());
        bot.setActive(dto.isActive());

        if (dto.getType() == Bot.BotType.INTERNAL) {
            bot.setPlan(dto.getPlan());
            bot.setPrice(dto.getPrice());
            bot.setCurrency(dto.getCurrency());
        } else {
            bot.setPlan(null);
            bot.setPrice(null);
            bot.setCurrency(null);
        }

        // Tags
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(
                    dto.getTagIds().stream().map(Integer::valueOf).toList() // Tag.id es int -> puedes mapear a long o crear findByTagIdIn
            );
            bot.setTags(new HashSet<>(tags));
        } else {
            bot.setTags(null);
        }
    }

    private AecobotCardDTO toAecobotCardDTO(Bot b, UserProfile user) {
        boolean hasAccess = false;
        if (user != null && b.getType() == Bot.BotType.INTERNAL) {
            hasAccess = userBotAccessRepository.existsByUserProfileAndBot(user, b);
        }
        String planLabel = (b.getType() == Bot.BotType.INTERNAL && b.getPlan() != null) ? b.getPlan().name() : "N/A";

        return new AecobotCardDTO(
                b.getBotId(),
                b.getImageUrl(),
                b.getTitle(),
                b.getOwnerName(),
                planLabel,
                b.getPrice(),
                b.getCurrency(),
                hasAccess
        );
    }

    private ExternalToolCardDTO toExternalToolCardDTO(Bot b) {
        List<String> tagNames = (b.getTags() == null) ? List.of()
                : b.getTags().stream().map(Tag::getName).sorted().toList();

        return new ExternalToolCardDTO(
                b.getBotId(),
                b.getImageUrl(),
                b.getTitle(),
                b.getOwnerName(),
                tagNames
        );
    }
}

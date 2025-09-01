package com.aecode.webcoursesback.servicesimplement;
import com.aecode.webcoursesback.dtos.Bot.*;
import com.aecode.webcoursesback.entities.Bot.Bot;
import com.aecode.webcoursesback.entities.Bot.UserBotFavorite;
import com.aecode.webcoursesback.entities.Category;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.Bot.BotRepository;
import com.aecode.webcoursesback.repositories.Bot.UserBotFavoriteRepository;
import com.aecode.webcoursesback.repositories.CategoryRepository;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BotServiceImpl implements BotService {
    private final BotRepository botRepository;
    private final UserBotFavoriteRepository userBotFavoriteRepository;
    private final IUserProfileRepository userProfileRepository;
    private final CategoryRepository categoryRepository;


    // ===================== AECOBOTS (INTERNAL) =====================
    @Override
    @Transactional(readOnly = true)
    public Page<BotCardDTO> listAecobotsPaged(String clerkId, Long categoryId, Pageable pageable) {
        UserProfile user = loadUserByClerkIdOrNull(clerkId);
        Page<Bot> page = (categoryId == null)
                ? botRepository.findByTypeAndActiveTrue(Bot.BotType.INTERNAL, pageable)
                : botRepository.findActiveByTypeAndCategoryId(Bot.BotType.INTERNAL, categoryId, pageable);

        final Set<Long> favoriteIds = favoriteIdsForPage(user, page);
        return page.map(b -> toCardDTO(b, favoriteIds.contains(b.getBotId()), user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotCardDTO> listAecobotsAll(String clerkId, Long categoryId) {
        UserProfile user = loadUserByClerkIdOrNull(clerkId);
        List<Bot> bots = (categoryId == null)
                ? botRepository.findByTypeAndActiveTrueOrderByTitleAsc(Bot.BotType.INTERNAL)
                : botRepository.findActiveByTypeAndCategoryIds(Bot.BotType.INTERNAL, List.of(categoryId));

        final Set<Long> favoriteIds = favoriteIdsForList(user, bots);
        return bots.stream()
                .map(b -> toCardDTO(b, favoriteIds.contains(b.getBotId()), user))
                .toList();
    }

    // ===================== AI TOOLS (EXTERNAL) =====================
    @Override
    @Transactional(readOnly = true)
    public Page<BotCardDTO> listExternalToolsPaged(String clerkId, Long categoryId, Pageable pageable) {
        UserProfile user = loadUserByClerkIdOrNull(clerkId);
        Page<Bot> page = (categoryId == null)
                ? botRepository.findByTypeAndActiveTrue(Bot.BotType.EXTERNAL, pageable)
                : botRepository.findActiveByTypeAndCategoryId(Bot.BotType.EXTERNAL, categoryId, pageable);

        final Set<Long> favoriteIds = favoriteIdsForPage(user, page);
        return page.map(b -> toCardDTO(b, favoriteIds.contains(b.getBotId()), user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotCardDTO> listExternalToolsAll(String clerkId, Long categoryId) {
        UserProfile user = loadUserByClerkIdOrNull(clerkId);
        List<Bot> bots = (categoryId == null)
                ? botRepository.findByTypeAndActiveTrueOrderByTitleAsc(Bot.BotType.EXTERNAL)
                : botRepository.findActiveByTypeAndCategoryIds(Bot.BotType.EXTERNAL, List.of(categoryId));

        final Set<Long> favoriteIds = favoriteIdsForList(user, bots);
        return bots.stream()
                .map(b -> toCardDTO(b, favoriteIds.contains(b.getBotId()), user))
                .toList();
    }

    // ===================== FAVORITOS =====================
    @Override
    @Transactional(readOnly = true)
    public Page<BotCardDTO> listMyBotsPaged(String clerkId, String type, Pageable pageable) {
        UserProfile user = requireUserByClerkId(clerkId);
        // Recupera favoritos del usuario y filtra por tipo si llega
        List<UserBotFavorite> favs = userBotFavoriteRepository.findByUserProfile(user);
        List<Bot> bots = favs.stream()
                .map(UserBotFavorite::getBot)
                .filter(b -> type == null || b.getType().name().equalsIgnoreCase(type))
                .filter(Bot::isActive)
                .sorted(Comparator.comparing(Bot::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        // Paginado manual
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), bots.size());

        List<BotCardDTO> content = (start >= end) ? List.of()
                : bots.subList(start, end).stream()
                // En "Mis bots" todos son favoritos: favorite=true
                .map(b -> toCardDTO(b, true, user))
                .toList();

        return new PageImpl<>(content, pageable, bots.size());
    }

    @Override
    public void addFavorite(String clerkId, Long botId) {
        UserProfile user = requireUserByClerkId(clerkId);
        Bot bot = botRepository.findById(botId).orElseThrow(() -> new NoSuchElementException("Bot no encontrado"));
        if (!userBotFavoriteRepository.existsByUserProfileAndBot(user, bot)) {
            userBotFavoriteRepository.save(UserBotFavorite.builder().userProfile(user).bot(bot).build());
        }
    }

    @Override
    public void removeFavorite(String clerkId, Long botId) {
        UserProfile user = requireUserByClerkId(clerkId);
        Bot bot = botRepository.findById(botId).orElseThrow(() -> new NoSuchElementException("Bot no encontrado"));
        userBotFavoriteRepository.findByUserProfileAndBot(user, bot)
                .ifPresent(userBotFavoriteRepository::delete);
    }

    // ===================== CRUD =====================
    @Override
    public BotCardDTO createBot(BotCreateUpdateDTO dto) {
        Bot bot = new Bot();
        apply(dto, bot);
        bot = botRepository.save(bot);
        return toCardDTO(bot, false, null); // al crear, no sabemos favorito
    }

    @Override
    public BotCardDTO updateBot(Long botId, BotCreateUpdateDTO dto) {
        Bot bot = botRepository.findById(botId).orElseThrow(() -> new NoSuchElementException("Bot no encontrado"));
        apply(dto, bot);
        bot = botRepository.save(bot);
        return toCardDTO(bot, false, null);
    }

    @Override
    public void deleteBot(Long botId) {
        botRepository.deleteById(botId);
    }

    // ===================== LINK =====================
    @Override
    @Transactional(readOnly = true)
    public BotLinkDTO getBotLink(Long botId) {
        Bot bot = botRepository.findById(botId).orElseThrow(() -> new NoSuchElementException("Bot no encontrado"));
        return new BotLinkDTO(bot.getBotId(), bot.getRedirectUrl());
    }

    // ===================== Helpers =====================
    private UserProfile loadUserByClerkIdOrNull(String clerkId) {
        if (clerkId == null || clerkId.isBlank()) return null;
        return userProfileRepository.findByClerkId(clerkId).orElse(null);
    }

    private UserProfile requireUserByClerkId(String clerkId) {
        if (clerkId == null || clerkId.isBlank())
            throw new IllegalArgumentException("clerkId requerido");
        return userProfileRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    private String sanitizeBadge(String badge) {
        return (badge == null || badge.trim().isEmpty()) ? null : badge.trim();
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
            bot.setBadge(sanitizeBadge(dto.getBadge())); // ← ahora String libre
        } else {
            bot.setPlan(null);
            bot.setPrice(null);
            bot.setCurrency(null);
            bot.setBadge(null); // EXTERNAL no muestra badge
        }

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            bot.setCategories(new HashSet<>(categories));
        } else {
            bot.setCategories(null);
        }
    }

    private BotCardDTO toCardDTO(Bot b, boolean favorite, UserProfile user) {
        boolean hasAccess = false; // si luego lo manejas, optimízalo por lote

        List<String> categories = (b.getCategories() == null) ? List.of()
                : b.getCategories().stream()
                .map(Category::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        // Mostrar badge solo para INTERNAL
        String badgeLabel = (b.getType() == Bot.BotType.INTERNAL) ? sanitizeBadge(b.getBadge()) : null;

        return BotCardDTO.builder()
                .botId(b.getBotId())
                .imageUrl(b.getImageUrl())
                .title(b.getTitle())
                .categories(categories)
                .shortDescription(b.getShortDescription())
                .ownerName(b.getOwnerName())
                .favorite(favorite)
                .badge(badgeLabel)
                .price((b.getType() == Bot.BotType.INTERNAL && b.getPlan() == Bot.BotPlan.PAID) ? b.getPrice() : null)
                .currency((b.getType() == Bot.BotType.INTERNAL && b.getPlan() == Bot.BotPlan.PAID) ? b.getCurrency() : null)
                .hasAccess((b.getType() == Bot.BotType.INTERNAL) ? hasAccess : null)
                .build();
    }

    private Set<Long> favoriteIdsForPage(UserProfile user, Page<Bot> page) {
        if (user == null || page.isEmpty()) return Set.of();
        List<Long> ids = page.getContent().stream().map(Bot::getBotId).toList();
        if (ids.isEmpty()) return Set.of();
        return new HashSet<>(userBotFavoriteRepository.findFavoriteBotIdsByUserAndBotIdIn(user, ids));
        // Si quieres evitar IN() con lista grande, puedes trocear ids en batches.
    }

    private Set<Long> favoriteIdsForList(UserProfile user, List<Bot> bots) {
        if (user == null || bots.isEmpty()) return Set.of();
        List<Long> ids = bots.stream().map(Bot::getBotId).toList();
        if (ids.isEmpty()) return Set.of();
        return new HashSet<>(userBotFavoriteRepository.findFavoriteBotIdsByUserAndBotIdIn(user, ids));
    }
}

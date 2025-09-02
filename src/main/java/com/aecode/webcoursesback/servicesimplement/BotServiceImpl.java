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


    // ===================== INTERNAL =====================
    @Override
    @Transactional(readOnly = true)
    public Page<AecobotCardDTO> listAecobotsPaged(String clerkId, Long categoryId, Pageable pageable) {
        UserProfile user = loadUserByClerkIdOrNull(clerkId);
        Pageable sorted = withDefaultSort(pageable);
        Page<Bot> page = (categoryId == null)
                ? botRepository.findByTypeAndActiveTrue(Bot.BotType.INTERNAL, sorted)
                : botRepository.findActiveByTypeAndCategoryId(Bot.BotType.INTERNAL, categoryId, sorted);
        Set<Long> favIds = favoriteIdsForPage(user, page);
        return page.map(b -> toAecobotCardDTO(b, favIds.contains(b.getBotId()), user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AecobotCardDTO> listAecobotsAll(String clerkId, Long categoryId) {
        UserProfile user = loadUserByClerkIdOrNull(clerkId);
        List<Bot> bots = (categoryId == null)
                ? botRepository.findAllActiveByTypeOrdered(Bot.BotType.INTERNAL)
                : botRepository.findActiveByTypeAndCategoryIds(Bot.BotType.INTERNAL, List.of(categoryId));
        Set<Long> favIds = favoriteIdsForList(user, bots);
        return bots.stream().map(b -> toAecobotCardDTO(b, favIds.contains(b.getBotId()), user)).toList();
    }

    // ===================== EXTERNAL =====================
    @Override
    @Transactional(readOnly = true)
    public Page<ExternalToolCardDTO> listExternalToolsPaged(String clerkId, Long categoryId, Pageable pageable) {
        UserProfile user = loadUserByClerkIdOrNull(clerkId);
        Pageable sorted = withDefaultSort(pageable);
        Page<Bot> page = (categoryId == null)
                ? botRepository.findByTypeAndActiveTrue(Bot.BotType.EXTERNAL, sorted)
                : botRepository.findActiveByTypeAndCategoryId(Bot.BotType.EXTERNAL, categoryId, sorted);
        Set<Long> favIds = favoriteIdsForPage(user, page);
        return page.map(b -> toExternalToolCardDTO(b, favIds.contains(b.getBotId())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExternalToolCardDTO> listExternalToolsAll(String clerkId, Long categoryId) {
        UserProfile user = loadUserByClerkIdOrNull(clerkId);
        List<Bot> bots = (categoryId == null)
                ? botRepository.findAllActiveByTypeOrdered(Bot.BotType.EXTERNAL)
                : botRepository.findActiveByTypeAndCategoryIds(Bot.BotType.EXTERNAL, List.of(categoryId));
        Set<Long> favIds = favoriteIdsForList(user, bots);
        return bots.stream().map(b -> toExternalToolCardDTO(b, favIds.contains(b.getBotId()))).toList();
    }

    // Home: 1 destacado + 6
    @Override
    @Transactional(readOnly = true)
    public ExternalToolsHomeDTO listExternalToolsHome(String clerkId, Long categoryId) {
        UserProfile user = loadUserByClerkIdOrNull(clerkId);

        Bot highlighted = null;
        if (categoryId == null) {
            highlighted = botRepository
                    .findFirstByTypeAndActiveTrueAndHighlightedTrueOrderByOrderNumberAscTitleAsc(Bot.BotType.EXTERNAL)
                    .orElse(null);
        } else {
            // si filtras por categoría y quieres el destacado dentro del filtro:
            // toma el primero filtrado por category (si hay varios highlighted, el primero por orden)
            List<Bot> hl = botRepository.findActiveByTypeAndCategoryIds(Bot.BotType.EXTERNAL, List.of(categoryId));
            highlighted = hl.stream().filter(Bot::isHighlighted).findFirst().orElse(null);
        }

        // Si no hay destacado, tomamos el primero según orden
        if (highlighted == null) {
            List<Bot> first = (categoryId == null)
                    ? botRepository.findAllActiveByTypeOrdered(Bot.BotType.EXTERNAL)
                    : botRepository.findActiveByTypeAndCategoryIds(Bot.BotType.EXTERNAL, List.of(categoryId));
            if (!first.isEmpty()) highlighted = first.get(0);
        }

        // ... después de calcular `highlighted` (puede quedar null o no)
        final Long highlightedId = (highlighted != null ? highlighted.getBotId() : null);

        // Otros 6
        Pageable topSix = PageRequest.of(0, 6);
        List<Bot> others;
        if (categoryId == null) {
            // usa el helper que excluye por id (puede ser null)
            others = botRepository.findActiveByTypeOrderedExcluding(
                    Bot.BotType.EXTERNAL, highlightedId, topSix
            );
        } else {
            // filtra por categoría y excluye el destacado con el id capturado
            others = botRepository.findActiveByTypeAndCategoryIds(
                            Bot.BotType.EXTERNAL, List.of(categoryId))
                    .stream()
                    .filter(b -> highlightedId == null || !Objects.equals(b.getBotId(), highlightedId))
                    .limit(6)
                    .toList();
        }

        // favoritos por lote
        List<Bot> all = new ArrayList<>();
        if (highlighted != null) all.add(highlighted);
        all.addAll(others);
        Set<Long> favIds = favoriteIdsForList(user, all);

        ExternalToolCardDTO hiDto = (highlighted == null) ? null
                : toExternalToolCardDTO(highlighted, favIds.contains(highlighted.getBotId()));
        List<ExternalToolCardDTO> otherDtos = others.stream()
                .map(b -> toExternalToolCardDTO(b, favIds.contains(b.getBotId())))
                .toList();

        return ExternalToolsHomeDTO.builder()
                .highlighted(hiDto)
                .others(otherDtos)
                .build();
    }

    // ===================== FAVORITOS =====================
    @Override
    @Transactional(readOnly = true)
    public Page<AecobotCardDTO> listMyInternalBotsPaged(String clerkId, Long categoryId, Pageable pageable) {
        UserProfile user = requireUserByClerkId(clerkId);
        List<UserBotFavorite> favs = userBotFavoriteRepository.findByUserProfile(user);

        List<Bot> bots = favs.stream()
                .map(UserBotFavorite::getBot)
                .filter(b -> b.isActive() && b.getType() == Bot.BotType.INTERNAL)
                .filter(b -> categoryId == null || hasCategory(b, categoryId))  // <<--- filtro categoría
                .sorted(Comparator.comparing(Bot::getOrderNumber)
                        .thenComparing(Bot::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), bots.size());
        List<AecobotCardDTO> content = (start >= end) ? List.of()
                : bots.subList(start, end).stream().map(b -> toAecobotCardDTO(b, true, user)).toList();
        return new PageImpl<>(content, pageable, bots.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExternalToolCardDTO> listMyExternalBotsPaged(String clerkId, Long categoryId, Pageable pageable) {
        UserProfile user = requireUserByClerkId(clerkId);
        List<UserBotFavorite> favs = userBotFavoriteRepository.findByUserProfile(user);

        List<Bot> bots = favs.stream()
                .map(UserBotFavorite::getBot)
                .filter(b -> b.isActive() && b.getType() == Bot.BotType.EXTERNAL)
                .filter(b -> categoryId == null || hasCategory(b, categoryId))  // <<--- filtro categoría
                .sorted(Comparator.comparing(Bot::getOrderNumber)
                        .thenComparing(Bot::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), bots.size());
        List<ExternalToolCardDTO> content = (start >= end) ? List.of()
                : bots.subList(start, end).stream().map(b -> toExternalToolCardDTO(b, true)).toList();
        return new PageImpl<>(content, pageable, bots.size());
    }

    // helper
    private boolean hasCategory(Bot b, Long categoryId) {
        if (b.getCategories() == null) return false;
        for (Category c : b.getCategories()) {
            if (Objects.equals(c.getCategoryId(), categoryId)) return true;
        }
        return false;
    }


    // ===================== CRUD =====================
    @Override
    public AecobotCardDTO createOrUpdateInternal(BotCreateUpdateDTO dto, Long botIdOrNull) {
        Bot bot = (botIdOrNull == null) ? new Bot()
                : botRepository.findById(botIdOrNull).orElseThrow(() -> new NoSuchElementException("Bot no encontrado"));

        apply(dto, bot); // llenamos campos comunes y específicos
        // Forzamos reglas INTERNAL
        bot.setType(Bot.BotType.INTERNAL);
        // highlighted no aplica a INTERNAL, siempre false
        bot.setHighlighted(false);

        bot = botRepository.save(bot);
        return toAecobotCardDTO(bot, false, null);
    }

    @Override
    public ExternalToolCardDTO createOrUpdateExternal(BotCreateUpdateDTO dto, Long botIdOrNull) {
        Bot bot = (botIdOrNull == null) ? new Bot()
                : botRepository.findById(botIdOrNull).orElseThrow(() -> new NoSuchElementException("Bot no encontrado"));

        apply(dto, bot);
        // Forzamos reglas EXTERNAL
        bot.setType(Bot.BotType.EXTERNAL);
        bot.setPlan(null);
        bot.setPrice(null);
        bot.setCurrency(null);
        bot.setBadge(null);

        bot = botRepository.save(bot);
        return toExternalToolCardDTO(bot, false);
    }

    @Override
    public void deleteBot(Long botId) {
        botRepository.deleteById(botId);
    }

    // ===================== Favorito toggle =====================
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

    // ===================== Helpers =====================
    private Pageable withDefaultSort(Pageable pageable) {
        Sort sort = Sort.by("orderNumber").ascending().and(Sort.by("title").ascending());
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

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

    private void apply(BotCreateUpdateDTO dto, Bot bot) {
        bot.setTitle(dto.getTitle());
        bot.setOwnerName(dto.getOwnerName());
        bot.setImageUrl(dto.getImageUrl());
        bot.setCoverImageUrl(dto.getCoverImageUrl());
        bot.setShortDescription(dto.getShortDescription());
        bot.setRedirectUrl(dto.getRedirectUrl());
        bot.setActive(dto.isActive());
        bot.setOrderNumber(dto.getOrderNumber() == null ? 0 : dto.getOrderNumber());
        bot.setHighlighted(Boolean.TRUE.equals(dto.getHighlighted()));

        if (dto.getType() == Bot.BotType.INTERNAL) {
            bot.setPlan(dto.getPlan());
            bot.setPrice(dto.getPrice());
            bot.setCurrency(dto.getCurrency());
            bot.setBadge(sanitize(dto.getBadge()));
        }

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            bot.setCategories(new HashSet<>(categories));
        } else {
            bot.setCategories(null);
        }
    }

    private String sanitize(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private AecobotCardDTO toAecobotCardDTO(Bot b, boolean favorite, UserProfile user) {
        boolean hasAccess = false; // si luego gestionas accesos, optimízalo por lote

        List<String> categories = (b.getCategories() == null) ? List.of()
                : b.getCategories().stream()
                .map(Category::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        return AecobotCardDTO.builder()
                .botId(b.getBotId())
                .imageUrl(b.getImageUrl())
                .title(b.getTitle())
                .categories(categories)
                .shortDescription(b.getShortDescription())
                .ownerName(b.getOwnerName())
                .favorite(favorite)
                .badge(b.getBadge())
                .price((b.getPlan() == Bot.BotPlan.PAID) ? b.getPrice() : null)
                .currency((b.getPlan() == Bot.BotPlan.PAID) ? b.getCurrency() : null)
                .hasAccess(hasAccess)
                .redirectUrl(b.getRedirectUrl())
                .orderNumber(b.getOrderNumber())
                .build();
    }

    private ExternalToolCardDTO toExternalToolCardDTO(Bot b, boolean favorite) {
        List<String> categories = (b.getCategories() == null) ? List.of()
                : b.getCategories().stream()
                .map(Category::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        return ExternalToolCardDTO.builder()
                .botId(b.getBotId())
                .coverImageUrl(b.getCoverImageUrl())
                .logoImageUrl(b.getImageUrl())
                .title(b.getTitle())
                .categories(categories)
                .shortDescription(b.getShortDescription())
                .favorite(favorite)
                .redirectUrl(b.getRedirectUrl())
                .highlighted(b.isHighlighted())
                .orderNumber(b.getOrderNumber())
                .build();
    }

    private Set<Long> favoriteIdsForPage(UserProfile user, Page<Bot> page) {
        if (user == null || page.isEmpty()) return Set.of();
        List<Long> ids = page.getContent().stream().map(Bot::getBotId).toList();
        if (ids.isEmpty()) return Set.of();
        return new HashSet<>(userBotFavoriteRepository.findFavoriteBotIdsByUserAndBotIdIn(user, ids));
    }

    private Set<Long> favoriteIdsForList(UserProfile user, List<Bot> bots) {
        if (user == null || bots.isEmpty()) return Set.of();
        List<Long> ids = bots.stream().map(Bot::getBotId).toList();
        if (ids.isEmpty()) return Set.of();
        return new HashSet<>(userBotFavoriteRepository.findFavoriteBotIdsByUserAndBotIdIn(user, ids));
    }
}

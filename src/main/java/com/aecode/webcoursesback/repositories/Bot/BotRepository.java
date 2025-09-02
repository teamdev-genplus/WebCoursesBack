package com.aecode.webcoursesback.repositories.Bot;

import com.aecode.webcoursesback.entities.Bot.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BotRepository extends JpaRepository<Bot, Long> {
    // ALL ordered (usando Pageable con Sort por orderNumber asc, title asc)
    Page<Bot> findByTypeAndActiveTrue(Bot.BotType type, Pageable pageable);

    // ALL (full list) ORDER BY orderNumber, title
    @Query("""
        SELECT b FROM Bot b
        WHERE b.active = true AND b.type = :type
        ORDER BY b.orderNumber ASC, b.title ASC
    """)
    List<Bot> findAllActiveByTypeOrdered(@Param("type") Bot.BotType type);

    // Category filtered (list)
    @Query("""
        SELECT DISTINCT b FROM Bot b
        JOIN b.categories c
        WHERE b.active = true
          AND b.type = :type
          AND c.categoryId IN :categoryIds
        ORDER BY b.orderNumber ASC, b.title ASC
    """)
    List<Bot> findActiveByTypeAndCategoryIds(@Param("type") Bot.BotType type,
                                             @Param("categoryIds") List<Long> categoryIds);

    // Category filtered (paged)
    @Query(value = """
        SELECT DISTINCT b FROM Bot b
        JOIN b.categories c
        WHERE b.active = true
          AND b.type = :type
          AND c.categoryId = :categoryId
        """,
            countQuery = """
        SELECT COUNT(DISTINCT b) FROM Bot b
        JOIN b.categories c
        WHERE b.active = true
          AND b.type = :type
          AND c.categoryId = :categoryId
        """)
    Page<Bot> findActiveByTypeAndCategoryId(@Param("type") Bot.BotType type,
                                            @Param("categoryId") Long categoryId,
                                            Pageable pageable);

    // --- Helpers para "destacado + 6" (EXTERNAL) ---
    Optional<Bot> findFirstByTypeAndActiveTrueAndHighlightedTrueOrderByOrderNumberAscTitleAsc(Bot.BotType type);

    @Query("""
        SELECT b FROM Bot b
        WHERE b.active = true
          AND b.type = :type
          AND (:excludeId IS NULL OR b.botId <> :excludeId)
        ORDER BY b.orderNumber ASC, b.title ASC
    """)
    List<Bot> findActiveByTypeOrderedExcluding(@Param("type") Bot.BotType type,
                                               @Param("excludeId") Long excludeId,
                                               Pageable pageable);
}

package com.aecode.webcoursesback.repositories.Bot;

import com.aecode.webcoursesback.entities.Bot.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BotRepository extends JpaRepository<Bot, Long> {
    // Listas por tipo y activos
    List<Bot> findByTypeAndActiveTrueOrderByTitleAsc(Bot.BotType type);

    // Paginado por tipo
    Page<Bot> findByTypeAndActiveTrue(Bot.BotType type, Pageable pageable);

    // Filtro por categoría (lista completa)
    @Query("""
        SELECT DISTINCT b FROM Bot b
        JOIN b.categories c
        WHERE b.active = true
          AND b.type = :type
          AND c.categoryId IN :categoryIds
        ORDER BY b.title ASC
    """)
    List<Bot> findActiveByTypeAndCategoryIds(@Param("type") Bot.BotType type,
                                             @Param("categoryIds") List<Long> categoryIds);

    // Paginado con filtro por categoría
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
}

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

    // Filtrar por tags
    @Query("""
           SELECT DISTINCT b FROM Bot b
           JOIN b.tags t
           WHERE b.active = true
             AND b.type = :type
             AND t.tagId IN :tagIds
           ORDER BY b.title ASC
           """)
    List<Bot> findActiveByTypeAndTagIds(@Param("type") Bot.BotType type, @Param("tagIds") List<Integer> tagIds);
}

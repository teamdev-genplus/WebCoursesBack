package com.aecode.webcoursesback.repositories.LiveANDShort;
import com.aecode.webcoursesback.entities.LiveANDShort.LiveEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LiveEventRepository extends JpaRepository<LiveEvent, Long> {
    // Destacados activos ordenados
    List<LiveEvent> findByActiveTrueAndHighlightedTrueOrderByHighlightOrderAsc();

    @Query("SELECT l FROM LiveEvent l WHERE l.active = true AND l.startDateTime >= :now ORDER BY l.startDateTime ASC")
    List<LiveEvent> findUpcoming(@Param("now") LocalDateTime now); // sin paginación

    // Pasados paginados
    @Query(value = "SELECT l FROM LiveEvent l WHERE l.active = true AND l.startDateTime < :now ORDER BY l.startDateTime DESC",
            countQuery = "SELECT COUNT(l) FROM LiveEvent l WHERE l.active = true AND l.startDateTime < :now")
    Page<LiveEvent> findPast(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("""
           SELECT DISTINCT l FROM LiveEvent l 
           JOIN l.tags t 
           WHERE l.active = true 
           AND t.tagId IN :tagIds 
           AND l.id <> :liveId
           ORDER BY l.startDateTime DESC
           """)
    List<LiveEvent> findRelatedByTagIds(@Param("tagIds") List<Integer> tagIds, @Param("liveId") Long liveId);
}

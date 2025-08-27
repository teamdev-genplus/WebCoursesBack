package com.aecode.webcoursesback.repositories.LiveANDShort;

import com.aecode.webcoursesback.entities.LiveANDShort.ShortVideo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortVideoRepository extends JpaRepository<ShortVideo, Long> {
    // Home: top N activos (sin paginar)
    @Query(nativeQuery = true, value = """
        SELECT * FROM short_videos 
        WHERE active = true 
        ORDER BY published_at DESC NULLS LAST 
        LIMIT :limit
    """)
    List<ShortVideo> findTopNActiveOrderByPublishedAtDesc(@Param("limit") int limit);

    // Paginado (opcional)
    @Query(value = "SELECT s FROM ShortVideo s WHERE s.active = true ORDER BY s.publishedAt DESC NULLS LAST",
            countQuery = "SELECT COUNT(s) FROM ShortVideo s WHERE s.active = true")
    Page<ShortVideo> findActivePaged(Pageable pageable);

    @Query("""
           SELECT DISTINCT s FROM ShortVideo s 
           JOIN s.tags t 
           WHERE s.active = true 
           AND t.tagId IN :tagIds 
           ORDER BY s.publishedAt DESC
           """)
    List<ShortVideo> findRelatedByTagIds(@Param("tagIds") List<Integer> tagIds);
}

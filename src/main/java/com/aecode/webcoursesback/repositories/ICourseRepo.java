package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICourseRepo extends JpaRepository<Course,Long>, JpaSpecificationExecutor<Course> {

    // ====== PÚBLICO (excluye EXCLUSIVO) ======

    // Listado general excluyendo EXCLUSIVO
    Page<Course> findByModeNot(Course.Mode excluded, Pageable pageable);

    // Por tipo excluyendo EXCLUSIVO
    Page<Course> findByTypeAndModeNot(String type, Course.Mode excluded, Pageable pageable);

    // Destacados excluyendo EXCLUSIVO
    List<Course> findByHighlightedTrueAndModeNotOrderByOrderNumberAsc(Course.Mode excluded);

    // Búsqueda por título excluyendo EXCLUSIVO
    List<Course> findByTitleIgnoreCaseContainingAndModeNot(String title, Course.Mode excluded);

    // Por tipo + duración (entre) excluyendo EXCLUSIVO
    @Query("""
        SELECT c FROM Course c
        WHERE c.type = :type
          AND c.mode <> :excluded
          AND c.cantTotalHours BETWEEN :minHours AND :maxHours
        """)
    Page<Course> findByTypeAndDurationBetweenExcludingExclusive(
            @Param("type") String type,
            @Param("excluded") Course.Mode excluded,
            @Param("minHours") Integer minHours,
            @Param("maxHours") Integer maxHours,
            Pageable pageable
    );

    // Por tipo + duración (>=) excluyendo EXCLUSIVO
    @Query("""
        SELECT c FROM Course c
        WHERE c.type = :type
          AND c.mode <> :excluded
          AND c.cantTotalHours >= :minHours
        """)
    Page<Course> findByTypeAndDurationGteExcludingExclusive(
            @Param("type") String type,
            @Param("excluded") Course.Mode excluded,
            @Param("minHours") Integer minHours,
            Pageable pageable
    );

    // Por tipo + tags excluyendo EXCLUSIVO
    @Query("""
        SELECT DISTINCT c FROM Course c
        JOIN c.modules m
        JOIN m.tags t
        WHERE c.type = :type
          AND c.mode <> :excluded
          AND t.tagId IN :tagIds
    """)
    Page<Course> findDistinctByTypeAndModulesTagsInExcludingExclusive(
            @Param("type") String type,
            @Param("excluded") Course.Mode excluded,
            @Param("tagIds") List<Long> tagIds,
            Pageable pageable
    );

    // ====== ADMIN (solo EXCLUSIVO) ======

    Page<Course> findByMode(Course.Mode mode, Pageable pageable);

    // Si quieres por tipo exclusivo:
    Page<Course> findByTypeAndMode(String type, Course.Mode mode, Pageable pageable);

    // ====== existentes (compat / uso puntual) ======
    // Favoritos por ids (se mantiene para quien lo use)
    Page<Course> findByCourseIdIn(List<Long> courseIds, Pageable pageable);

    // Favoritos por ids y tipo
    Page<Course> findByCourseIdInAndType(List<Long> courseIds, String type, Pageable pageable);

    Optional<Course> findByUrlnamecourse(String urlnamecourse);

}

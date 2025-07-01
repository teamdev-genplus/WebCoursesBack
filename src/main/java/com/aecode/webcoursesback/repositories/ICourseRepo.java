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

@Repository
public interface ICourseRepo extends JpaRepository<Course,Long>, JpaSpecificationExecutor<Course> {

    //Encontrar curso por tipo y paginarlo
    Page<Course> findByType(String type, Pageable pageable);

    //Obtener los cursos destacados
    List<Course> findByHighlightedTrueOrderByOrderNumberAsc();

    // Busca cursos cuyo título contenga el texto dado (ignora mayúsculas/minúsculas)
    List<Course> findByTitleIgnoreCaseContaining(String title);

    //Para listar por modalidad
    Page<Course> findByMode(Course.Mode mode, Pageable pageable);

    //filtro por rango de horas
    Page<Course> findByCantTotalHoursBetween(Integer minHours, Integer maxHours, Pageable pageable);
    Page<Course> findByCantTotalHoursGreaterThanEqual(Integer minHours, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Course c JOIN c.modules m JOIN m.tags t WHERE t.tagId IN :tagIds")
    Page<Course> findDistinctByModulesTagsIn(@Param("tagIds") List<Long> tagIds, Pageable pageable);

    //para filtro de favoritos
    Page<Course> findByCourseIdIn(List<Long> courseIds, Pageable pageable);
}

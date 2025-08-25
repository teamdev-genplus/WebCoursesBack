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

    //filtro de favoritos
    Page<Course> findByCourseIdIn(List<Long> courseIds, Pageable pageable);


    //Filtros
    // ✅ Nuevo: Buscar por type y tags (relación many-to-many entre módulos y tags)
    @Query("""
        SELECT DISTINCT c FROM Course c 
        JOIN c.modules m 
        JOIN m.tags t 
        WHERE c.type = :type AND t.tagId IN :tagIds
    """)
    Page<Course> findDistinctByTypeAndModulesTagsIn(@Param("type") String type, @Param("tagIds") List<Long> tagIds, Pageable pageable);

    // ✅ Nuevo: Buscar por type y modo
    Page<Course> findByTypeAndMode(String type, Course.Mode mode, Pageable p);

    // ✅ Nuevo: Buscar por type y rango de duración
    Page<Course> findByTypeAndCantTotalHoursBetween(String type, Integer minHours, Integer maxHours, Pageable pageable);

    // ✅ Nuevo: Buscar por type y duración mayor o igual a cierto número
    Page<Course> findByTypeAndCantTotalHoursGreaterThanEqual(String type, Integer minHours, Pageable pageable);

    //Favoritos
    Page<Course> findByCourseIdInAndType(List<Long> courseIds, String type, Pageable pageable);



}

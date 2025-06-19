package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

@Repository
public interface ICourseRepo extends JpaRepository<Course,Long>, JpaSpecificationExecutor<Course> {

    //Encontrar curso por tipo y paginarlo
    Page<Course> findByType(String type, Pageable pageable);

    //Obtener los cursos destacados
    List<Course> findByHighlightedTrueOrderByOrderNumberAsc();
}

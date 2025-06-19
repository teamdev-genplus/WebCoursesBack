package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.CourseCardDTO;
import com.aecode.webcoursesback.dtos.HighlightedCourseDTO;
import com.aecode.webcoursesback.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICourseService {

    List<Course> listAll();
    void delete(Long courseId);

    //Mostrar cards de todos los cursos
    Page<CourseCardDTO> getAllCourseCards(Pageable pageable);
    //MOSTRAR CARDS DE LOS CURSOS POR TIPO Y PAGINADOS
    Page<CourseCardDTO> getCourseCardsByType(String type, Pageable pageable);

    List<HighlightedCourseDTO> getAllHighlightedCourses();
}

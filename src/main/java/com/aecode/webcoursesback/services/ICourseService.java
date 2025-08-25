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

    // (público) sin favoritos
    Page<CourseCardDTO> getAllCourseCards(Pageable pageable);
    Page<CourseCardDTO> getCourseCardsByType(String type, Pageable pageable);

    // (autenticado) con favoritos
    Page<CourseCardDTO> getAllCourseCards(String clerkId, Pageable pageable);
    Page<CourseCardDTO> getCourseCardsByType(String type, String clerkId, Pageable pageable);


    ///////////////////////


    List<HighlightedCourseDTO> getAllHighlightedCourses();

    // Service: Servicio para listar cursos filtrados por título
    List<CourseCardDTO> findCoursesByTitle(String title);

    // Filtros (te propongo overloads con clerkId)
    Page<CourseCardDTO> getCourseCardsByModeAndType(String mode, String type, Pageable pageable);
    Page<CourseCardDTO> getCourseCardsByModeAndType(String mode, String type, String clerkId, Pageable pageable);

    Page<CourseCardDTO> getCourseCardsByDurationRangeAndType(String range, String type, Pageable pageable);
    Page<CourseCardDTO> getCourseCardsByDurationRangeAndType(String range, String type, String clerkId, Pageable pageable);

    Page<CourseCardDTO> getCoursesByModuleTagsAndType(String type, List<Long> tagIds, Pageable pageable);
    Page<CourseCardDTO> getCoursesByModuleTagsAndType(String type, List<Long> tagIds, String clerkId, Pageable pageable);
}

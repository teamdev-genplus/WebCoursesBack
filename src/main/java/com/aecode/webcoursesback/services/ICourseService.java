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

    // PÚBLICO: excluye EXCLUSIVO
    Page<CourseCardDTO> getAllCourseCards(Pageable pageable);
    Page<CourseCardDTO> getCourseCardsByType(String type, Pageable pageable);

    // AUTENTICADO (clerkId): excluye EXCLUSIVO
    Page<CourseCardDTO> getAllCourseCards(String clerkId, Pageable pageable);
    Page<CourseCardDTO> getCourseCardsByType(String type, String clerkId, Pageable pageable);

    // Destacados (excluye EXCLUSIVO)
    List<HighlightedCourseDTO> getAllHighlightedCourses();

    // Búsqueda por título (excluye EXCLUSIVO)
    List<CourseCardDTO> findCoursesByTitle(String title);

    // Filtros:
    Page<CourseCardDTO> getCourseCardsByModeAndType(String mode, String type, Pageable pageable);
    Page<CourseCardDTO> getCourseCardsByModeAndType(String mode, String type, String clerkId, Pageable pageable);

    Page<CourseCardDTO> getCourseCardsByDurationRangeAndType(String range, String type, Pageable pageable);
    Page<CourseCardDTO> getCourseCardsByDurationRangeAndType(String range, String type, String clerkId, Pageable pageable);

    Page<CourseCardDTO> getCoursesByModuleTagsAndType(String type, List<Long> tagIds, Pageable pageable);
    Page<CourseCardDTO> getCoursesByModuleTagsAndType(String type, List<Long> tagIds, String clerkId, Pageable pageable);

    // ADMIN: solo EXCLUSIVO
    Page<CourseCardDTO> getExclusiveCourseCards(Pageable pageable);
    Page<CourseCardDTO> getExclusiveCourseCards(String clerkId, Pageable pageable);
}

package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.services.ICourseService;
import com.aecode.webcoursesback.services.IUserFavoriteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private ICourseService cS;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private IUserFavoriteService userFavoriteService;
    //----------------------------------------------------------GET----------------------------------------------------------------

    @GetMapping("/listall")
    public ResponseEntity<List<CourseCardDTO>> listAll() {
        List<Course> courses = cS.listAll();
        List<CourseCardDTO> coursescardDTO = courses.stream()
                .map(course -> modelMapper.map(course, CourseCardDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(coursescardDTO, HttpStatus.OK);
    }

    // Obtener cards paginados y filtrados por tipo
    @GetMapping("/cards/type")
    public ResponseEntity<Page<CourseCardDTO>> getCourseCards(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> cardsPage;

        if (type == null || type.isEmpty()) {
            // Si no se especifica tipo, traer todos los cursos
            cardsPage = cS.getAllCourseCards(pageable);
        } else {
            // Si se especifica tipo, filtrar por tipo
            cardsPage = cS.getCourseCardsByType(type, pageable);
        }

        return new ResponseEntity<>(cardsPage, HttpStatus.OK);
    }

    // Obtener cards paginados y filtrados por modalidad
    @GetMapping("/cards/mode")
    public ResponseEntity<Page<CourseCardDTO>> getCourseCardsByMode(
            @RequestParam(defaultValue = "TODOS") String mode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> cardsPage = cS.getCourseCardsByMode(mode, pageable);

        return new ResponseEntity<>(cardsPage, HttpStatus.OK);
    }


    //----------------------------------------------------------FILTROS----------------------------------------------------------------
    //OBTENER LOS CURSOS DESTACADOS
    @GetMapping("/courses/highlighted")
    public ResponseEntity<List<HighlightedCourseDTO>> getAllHighlightedCourses() {
        List<HighlightedCourseDTO> highlightedCourses = cS.getAllHighlightedCourses();
        return ResponseEntity.ok(highlightedCourses);
    }


    //Método para buscar cursos por título
    @GetMapping("/courses/search")
    public ResponseEntity<List<CourseCardDTO>> searchCoursesByTitle(@RequestParam String title) {
        List<CourseCardDTO> courses = cS.findCoursesByTitle(title);
        return ResponseEntity.ok(courses);
    }

    // Obtener cards paginados por rango de horas
    @GetMapping("/cards/duration")
    public ResponseEntity<Page<CourseCardDTO>> getCourseCardsByDuration(
            @RequestParam String range,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> cardsPage = cS.getCourseCardsByDurationRange(range, pageable);

        return new ResponseEntity<>(cardsPage, HttpStatus.OK);
    }

    // Obtener cards paginados por tags
    @GetMapping("/courses/filterByTags")
    public ResponseEntity<Page<CourseCardDTO>> getCoursesByTags(
            @RequestParam List<Long> tagIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> courses = cS.getCoursesByModuleTags(tagIds, pageable);
        return ResponseEntity.ok(courses);
    }

    // Obtener cards paginadas de favoritos
    @GetMapping("/cards/favorites")
    public ResponseEntity<Page<CourseCardDTO>> getFavoriteCourseCards(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> favoriteCourses = userFavoriteService.getFavoriteCoursesByUser(userId, pageable);
        return ResponseEntity.ok(favoriteCourses);
    }

    //----------------------------------------------------------DELETE----------------------------------------------------------------
     @DeleteMapping("/{courseId}")
    public void delete(@PathVariable Long courseId) {
         cS.delete(courseId);
    }


    //----------------------------------------------------------EXCEPTIONS----------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}

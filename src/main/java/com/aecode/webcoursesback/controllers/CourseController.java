package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.services.ICourseService;
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
public class CourseController  {

    @Autowired
    private ICourseService cS;

    private final ModelMapper modelMapper = new ModelMapper();

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
    @GetMapping("/cards")
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

    //OBTENER LOS CURSOS DESTACADOS
    @GetMapping("/courses/highlighted")
    public ResponseEntity<List<HighlightedCourseDTO>> getAllHighlightedCourses() {
        List<HighlightedCourseDTO> highlightedCourses = cS.getAllHighlightedCourses();
        return ResponseEntity.ok(highlightedCourses);
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

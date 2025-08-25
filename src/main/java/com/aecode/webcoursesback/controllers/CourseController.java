package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.CourseCardDTO;
import com.aecode.webcoursesback.dtos.HighlightedCourseDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.services.ICourseService;
import com.aecode.webcoursesback.services.IUserFavoriteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private ICourseService cS;

    @Autowired
    private IUserFavoriteService userFavoriteService;

    private final ModelMapper modelMapper = new ModelMapper();

    // --------------------------------------------------------------------------------------
    // LISTA SIMPLE (compatibilidad; no marca favoritos)
    // --------------------------------------------------------------------------------------
    @GetMapping("/listallcards")
    public ResponseEntity<List<CourseCardDTO>> listAll() {
        List<Course> courses = cS.listAll();
        List<CourseCardDTO> coursescardDTO = courses.stream()
                .map(c -> CourseCardDTO.builder()
                        .courseId(c.getCourseId())
                        .principalImage(c.getPrincipalImage())
                        .title(c.getTitle())
                        .orderNumber(c.getOrderNumber())
                        .type(c.getType())
                        .cantModOrHours(c.getCantModOrHours())
                        .mode(c.getMode())
                        .urlnamecourse(c.getUrlnamecourse())
                        .favorite(false) // este endpoint no usa clerkId
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(coursescardDTO);
    }

    // --------------------------------------------------------------------------------------
    // CARDS POR TIPO (clerkId opcional: si viene, marcamos favoritos)
    // Ej.: /courses/cards/type?type=diplomado&clerkId=user_abc&page=0&size=8&sortBy=orderNumber
    // --------------------------------------------------------------------------------------
    @GetMapping("/cards/type")
    public ResponseEntity<Page<CourseCardDTO>> getCourseCards(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String clerkId,  // opcional
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> cardsPage = (type == null || type.isBlank())
                ? cS.getAllCourseCards(clerkId, pageable)          // con favoritos si clerkId != null
                : cS.getCourseCardsByType(type, clerkId, pageable);

        return ResponseEntity.ok(cardsPage);
    }

    // --------------------------------------------------------------------------------------
    // CARDS POR MODALIDAD + TIPO (clerkId opcional)
    // Ej.: /courses/cards/mode/by-type?type=diplomado&mode=ASINCRONO&clerkId=user_abc
    // --------------------------------------------------------------------------------------
    @GetMapping("/cards/mode/by-type")
    public ResponseEntity<Page<CourseCardDTO>> getCourseCardsByModeAndType(
            @RequestParam String type,
            @RequestParam String mode,
            @RequestParam(required = false) String clerkId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> cardsPage = (clerkId == null || clerkId.isBlank())
                ? cS.getCourseCardsByModeAndType(mode, type, pageable)
                : cS.getCourseCardsByModeAndType(mode, type, clerkId, pageable);
        return ResponseEntity.ok(cardsPage);
    }

    // --------------------------------------------------------------------------------------
    // CARDS POR RANGO DE HORAS + TIPO (clerkId opcional)
    // Ej.: /courses/cards/duration/by-type?type=diplomado&range=10-20&clerkId=user_abc
    // --------------------------------------------------------------------------------------
    @GetMapping("/cards/duration/by-type")
    public ResponseEntity<Page<CourseCardDTO>> getCourseCardsByDurationRangeAndType(
            @RequestParam String type,
            @RequestParam String range,
            @RequestParam(required = false) String clerkId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> cardsPage = (clerkId == null || clerkId.isBlank())
                ? cS.getCourseCardsByDurationRangeAndType(range, type, pageable)
                : cS.getCourseCardsByDurationRangeAndType(range, type, clerkId, pageable);
        return ResponseEntity.ok(cardsPage);
    }

    // --------------------------------------------------------------------------------------
    // CARDS POR TAGS + TIPO (clerkId opcional)
    // Ej.: /courses/cards/filterByTags/by-type?type=diplomado&tagIds=1,2,3&clerkId=user_abc
    // --------------------------------------------------------------------------------------
    @GetMapping("/cards/filterByTags/by-type")
    public ResponseEntity<Page<CourseCardDTO>> getCoursesByTagsAndType(
            @RequestParam String type,
            @RequestParam List<Long> tagIds,
            @RequestParam(required = false) String clerkId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> cardsPage = (clerkId == null || clerkId.isBlank())
                ? cS.getCoursesByModuleTagsAndType(type, tagIds, pageable)
                : cS.getCoursesByModuleTagsAndType(type, tagIds, clerkId, pageable);
        return ResponseEntity.ok(cardsPage);
    }

    // --------------------------------------------------------------------------------------
    // CARDS DESTACADOS (no depende de favoritos)
    // --------------------------------------------------------------------------------------
    @GetMapping("/courses/highlighted")
    public ResponseEntity<List<HighlightedCourseDTO>> getAllHighlightedCourses() {
        return ResponseEntity.ok(cS.getAllHighlightedCourses());
    }

    // --------------------------------------------------------------------------------------
    // BÚSQUEDA POR TÍTULO (listado simple; si quieres marcar favoritos, crea overload con clerkId)
    // --------------------------------------------------------------------------------------
    @GetMapping("/courses/search")
    public ResponseEntity<List<CourseCardDTO>> searchCoursesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(cS.findCoursesByTitle(title));
    }

    // --------------------------------------------------------------------------------------
    // FAVORITOS PAGINADOS (solo favoritos del usuario por tipo)
    // Ej.: /courses/cards/favorites?clerkId=user_abc&type=diplomado
    // --------------------------------------------------------------------------------------
    @GetMapping("/cards/favorites")
    public ResponseEntity<Page<CourseCardDTO>> getFavoriteCourseCards(
            @RequestParam String clerkId,
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> favoriteCourses =
                userFavoriteService.getFavoriteCoursesByUserAndType(clerkId, type, pageable);
        return ResponseEntity.ok(favoriteCourses);
    }

    // --------------------------------------------------------------------------------------
    // DELETE (administrativo)
    // --------------------------------------------------------------------------------------
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> delete(@PathVariable Long courseId) {
        cS.delete(courseId);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------------------------------------
    // EXCEPCIONES
    // --------------------------------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}

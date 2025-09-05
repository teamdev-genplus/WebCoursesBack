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
    // LISTA SIMPLE (compat) -> EXCLUYE EXCLUSIVO
    // --------------------------------------------------------------------------------------
    @GetMapping("/listallcards")
    public ResponseEntity<List<CourseCardDTO>> listAll() {
        List<Course> courses = cS.listAll();
        List<CourseCardDTO> coursescardDTO = courses.stream()
                .filter(c -> c.getMode() != Course.Mode.EXCLUSIVO) // excluye
                .map(c -> CourseCardDTO.builder()
                        .courseId(c.getCourseId())
                        .principalImage(c.getPrincipalImage())
                        .title(c.getTitle())
                        .orderNumber(c.getOrderNumber())
                        .type(c.getType())
                        .cantModOrHours(c.getCantModOrHours())
                        .mode(c.getMode())
                        .urlnamecourse(c.getUrlnamecourse())
                        .favorite(false)
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(coursescardDTO);
    }

    // --------------------------------------------------------------------------------------
    // CARDS POR TIPO (clerkId opcional) -> service ya excluye EXCLUSIVO
    // --------------------------------------------------------------------------------------
    @GetMapping("/cards/type")
    public ResponseEntity<Page<CourseCardDTO>> getCourseCards(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String clerkId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> cardsPage = (type == null || type.isBlank())
                ? cS.getAllCourseCards(clerkId, pageable)
                : cS.getCourseCardsByType(type, clerkId, pageable);

        return ResponseEntity.ok(cardsPage);
    }

    // --------------------------------------------------------------------------------------
    // CARDS POR MODALIDAD + TIPO (clerkId opcional) -> service ya excluye EXCLUSIVO
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
    // CARDS POR RANGO DE HORAS + TIPO (clerkId opcional) -> service ya excluye EXCLUSIVO
    // --------------------------------------------------------------------------------------
    @GetMapping("/cards/duration/by-type")
    public ResponseEntity<Page<CourseCardDTO>> getCourseCardsByDurationRangeAndType(
            @RequestParam String type,
            @RequestParam String range, // "1-9", "10-20", "+20"
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
    // CARDS POR TAGS + TIPO (clerkId opcional) -> service ya excluye EXCLUSIVO
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
    // DESTACADOS (excluye EXCLUSIVO en service)
    // --------------------------------------------------------------------------------------
    @GetMapping("/courses/highlighted")
    public ResponseEntity<List<HighlightedCourseDTO>> getAllHighlightedCourses() {
        return ResponseEntity.ok(cS.getAllHighlightedCourses());
    }

    // --------------------------------------------------------------------------------------
    // BÚSQUEDA POR TÍTULO (excluye EXCLUSIVO en service)
    // --------------------------------------------------------------------------------------
    @GetMapping("/courses/search")
    public ResponseEntity<List<CourseCardDTO>> searchCoursesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(cS.findCoursesByTitle(title));
    }

    // --------------------------------------------------------------------------------------
    // FAVORITOS PAGINADOS -> filtro en memoria EXCLUSIVO por si el servicio no lo hace
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

        // Filtro EXCLUSIVO en memoria (porque este endpoint depende de otro service)
        List<CourseCardDTO> filtered = favoriteCourses.getContent().stream()
                .filter(c -> c.getMode() != Course.Mode.EXCLUSIVO)
                .toList();

        Page<CourseCardDTO> result = new PageImpl<>(
                filtered,
                pageable,
                filtered.size() // total filtrado. Si quieres el total original, usa favoriteCourses.getTotalElements()
        );
        return ResponseEntity.ok(result);
    }

    // --------------------------------------------------------------------------------------
    // ADMIN: SOLO EXCLUSIVO (proteger con seguridad/roles en tu proyecto)
    // --------------------------------------------------------------------------------------
    @GetMapping("/admin/exclusive/cards")
    public ResponseEntity<Page<CourseCardDTO>> getExclusiveCardsAdmin(
            @RequestParam(required = false) String clerkId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "orderNumber") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CourseCardDTO> cards = (clerkId == null || clerkId.isBlank())
                ? cS.getExclusiveCourseCards(pageable)
                : cS.getExclusiveCourseCards(clerkId, pageable);
        return ResponseEntity.ok(cards);
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

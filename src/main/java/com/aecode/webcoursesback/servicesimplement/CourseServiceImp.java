package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.services.ICourseService;
import com.aecode.webcoursesback.services.IUserFavoriteService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import java.util.List;

@Service
public class CourseServiceImp implements ICourseService {
    @Autowired private ICourseRepo cR;
    @Autowired private IUserFavoriteService favoriteService;

    // ----------------- utilidades -----------------
    private Set<Long> favoritesOf(String clerkId) {
        if (clerkId == null || clerkId.isBlank()) return Collections.emptySet();
        return new HashSet<>(favoriteService.getFavoriteCourseIdsByUser(clerkId));
    }

    private CourseCardDTO mapToCourseCardDTO(Course c, boolean favorite) {
        return CourseCardDTO.builder()
                .courseId(c.getCourseId())
                .principalImage(c.getPrincipalImage())
                .title(c.getTitle())
                .orderNumber(c.getOrderNumber())
                .type(c.getType())
                .cantModOrHours(c.getCantModOrHours())
                .mode(c.getMode())
                .urlnamecourse(c.getUrlnamecourse())
                .favorite(favorite)
                .build();
    }

    private Page<CourseCardDTO> toCardPage(Page<Course> page, Set<Long> favs) {
        return page.map(c -> mapToCourseCardDTO(c, favs.contains(c.getCourseId())));
    }

    // ----------------- CRUD básicos -----------------
    @Override public List<Course> listAll() { return cR.findAll(); }
    @Override public void delete(Long courseId) { cR.deleteById(courseId); }

    // ----------------- Cards sin favoritos (público) -----------------
    @Override
    public Page<CourseCardDTO> getAllCourseCards(Pageable pageable) {
        return toCardPage(cR.findAll(pageable), Collections.emptySet());
    }

    @Override
    public Page<CourseCardDTO> getCourseCardsByType(String type, Pageable pageable) {
        return toCardPage(cR.findByType(type, pageable), Collections.emptySet());
    }

    // ----------------- Cards con favoritos (autenticado) -----------------
    @Override
    public Page<CourseCardDTO> getAllCourseCards(String clerkId, Pageable pageable) {
        return toCardPage(cR.findAll(pageable), favoritesOf(clerkId));
    }

    @Override
    public Page<CourseCardDTO> getCourseCardsByType(String type, String clerkId, Pageable pageable) {
        return toCardPage(cR.findByType(type, pageable), favoritesOf(clerkId));
    }

    // ----------------- Destacados y búsquedas -----------------
    @Override
    public List<HighlightedCourseDTO> getAllHighlightedCourses() {
        return cR.findByHighlightedTrueOrderByOrderNumberAsc()
                .stream()
                .map(c -> new HighlightedCourseDTO(c.getCourseId(), c.getTitle(), c.getDescription(), c.getHighlightImage()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseCardDTO> findCoursesByTitle(String title) {
        return cR.findByTitleIgnoreCaseContaining(title).stream()
                .map(c -> mapToCourseCardDTO(c, false))
                .collect(Collectors.toList());
    }

    // ----------------- Filtros por modalidad -----------------
    @Override
    public Page<CourseCardDTO> getCourseCardsByModeAndType(String modeStr, String type, Pageable pageable) {
        if (modeStr == null || modeStr.equalsIgnoreCase("TODOS")) {
            return toCardPage(cR.findByType(type, pageable), Collections.emptySet());
        }
        Course.Mode mode;
        try { mode = Course.Mode.valueOf(modeStr.toUpperCase()); }
        catch (IllegalArgumentException e) { return Page.empty(pageable); }
        return toCardPage(cR.findByTypeAndMode(type, mode, pageable), Collections.emptySet());
    }

    @Override
    public Page<CourseCardDTO> getCourseCardsByModeAndType(String modeStr, String type, String clerkId, Pageable pageable) {
        Set<Long> favs = favoritesOf(clerkId);
        if (modeStr == null || modeStr.equalsIgnoreCase("TODOS")) {
            return toCardPage(cR.findByType(type, pageable), favs);
        }
        Course.Mode mode;
        try { mode = Course.Mode.valueOf(modeStr.toUpperCase()); }
        catch (IllegalArgumentException e) { return Page.empty(pageable); }
        return toCardPage(cR.findByTypeAndMode(type, mode, pageable), favs);
    }

    // ----------------- Filtro por duración (sin favoritos) -----------------
    @Override
    public Page<CourseCardDTO> getCourseCardsByDurationRangeAndType(String range, String type, Pageable pageable) {
        Page<Course> courses = switch (range) {
            case "1-9"   -> cR.findByTypeAndCantTotalHoursBetween(type, 1, 9, pageable);
            case "10-20" -> cR.findByTypeAndCantTotalHoursBetween(type, 10, 20, pageable);
            case "+20"   -> cR.findByTypeAndCantTotalHoursGreaterThanEqual(type, 21, pageable);
            default      -> Page.empty(pageable); // Si llega un valor inesperado, devolver vacío
        };
        return toCardPage(courses, Collections.emptySet());
    }

    // ----------------- Filtro por duración (con favoritos) -----------------
    @Override
    public Page<CourseCardDTO> getCourseCardsByDurationRangeAndType(String range, String type, String clerkId, Pageable pageable) {
        Set<Long> favs = favoritesOf(clerkId);
        Page<Course> courses = switch (range) {
            case "1-9"   -> cR.findByTypeAndCantTotalHoursBetween(type, 1, 9, pageable);
            case "10-20" -> cR.findByTypeAndCantTotalHoursBetween(type, 10, 20, pageable);
            case "+20"   -> cR.findByTypeAndCantTotalHoursGreaterThanEqual(type, 21, pageable);
            default      -> Page.empty(pageable);
        };
        return toCardPage(courses, favs);
    }


    // ----------------- Filtro por tags -----------------
    @Override
    public Page<CourseCardDTO> getCoursesByModuleTagsAndType(String type, List<Long> tagIds, Pageable pageable) {
        Page<Course> page = (tagIds == null || tagIds.isEmpty())
                ? cR.findByType(type, pageable)
                : cR.findDistinctByTypeAndModulesTagsIn(type, tagIds, pageable);
        return toCardPage(page, Collections.emptySet());
    }

    @Override
    public Page<CourseCardDTO> getCoursesByModuleTagsAndType(String type, List<Long> tagIds, String clerkId, Pageable pageable) {
        Set<Long> favs = favoritesOf(clerkId);
        Page<Course> page = (tagIds == null || tagIds.isEmpty())
                ? cR.findByType(type, pageable)
                : cR.findDistinctByTypeAndModulesTagsIn(type, tagIds, pageable);
        return toCardPage(page, favs);
    }
}


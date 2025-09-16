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

    // ----------------- Cards sin favoritos (público) - EXCLUYE EXCLUSIVO -----------------
    @Override
    public Page<CourseCardDTO> getAllCourseCards(Pageable pageable) {
        return toCardPage(cR.findByModeNot(Course.Mode.EXCLUSIVO, pageable), Collections.emptySet());
    }

    @Override
    public Page<CourseCardDTO> getCourseCardsByType(String type, Pageable pageable) {
        return toCardPage(cR.findByTypeAndModeNot(type, Course.Mode.EXCLUSIVO, pageable), Collections.emptySet());
    }

    // ----------------- Cards con favoritos (autenticado) - EXCLUYE EXCLUSIVO -----------------
    @Override
    public Page<CourseCardDTO> getAllCourseCards(String clerkId, Pageable pageable) {
        return toCardPage(cR.findByModeNot(Course.Mode.EXCLUSIVO, pageable), favoritesOf(clerkId));
    }

    @Override
    public Page<CourseCardDTO> getCourseCardsByType(String type, String clerkId, Pageable pageable) {
        return toCardPage(cR.findByTypeAndModeNot(type, Course.Mode.EXCLUSIVO, pageable), favoritesOf(clerkId));
    }

    // ----------------- Destacados (excluye EXCLUSIVO) -----------------
    @Override
    public List<HighlightedCourseDTO> getAllHighlightedCourses() {
        return cR.findByHighlightedTrueAndModeNotOrderByOrderNumberAsc(Course.Mode.EXCLUSIVO)
                .stream()
                .map(c -> new HighlightedCourseDTO(c.getCourseId(), c.getTitle(), c.getDescription(), c.getHighlightImage(), c.getStartDate()))
                .collect(Collectors.toList());
    }

    // ----------------- Búsqueda por título (excluye EXCLUSIVO) -----------------
    @Override
    public List<CourseCardDTO> findCoursesByTitle(String title) {
        return cR.findByTitleIgnoreCaseContainingAndModeNot(title, Course.Mode.EXCLUSIVO).stream()
                .map(c -> mapToCourseCardDTO(c, false))
                .collect(Collectors.toList());
    }

    // ----------------- Filtros por modalidad (excluye EXCLUSIVO salvo admin) -----------------
    @Override
    public Page<CourseCardDTO> getCourseCardsByModeAndType(String modeStr, String type, Pageable pageable) {
        if (modeStr == null || modeStr.equalsIgnoreCase("TODOS")) {
            return toCardPage(cR.findByTypeAndModeNot(type, Course.Mode.EXCLUSIVO, pageable), Collections.emptySet());
        }
        Course.Mode mode;
        try { mode = Course.Mode.valueOf(modeStr.toUpperCase()); }
        catch (IllegalArgumentException e) { return Page.empty(pageable); }
        if (mode == Course.Mode.EXCLUSIVO) {
            // Público NO debe ver exclusivos
            return Page.empty(pageable);
        }
        return toCardPage(cR.findByTypeAndMode(type, mode, pageable), Collections.emptySet());
    }

    @Override
    public Page<CourseCardDTO> getCourseCardsByModeAndType(String modeStr, String type, String clerkId, Pageable pageable) {
        Set<Long> favs = favoritesOf(clerkId);
        if (modeStr == null || modeStr.equalsIgnoreCase("TODOS")) {
            return toCardPage(cR.findByTypeAndModeNot(type, Course.Mode.EXCLUSIVO, pageable), favs);
        }
        Course.Mode mode;
        try { mode = Course.Mode.valueOf(modeStr.toUpperCase()); }
        catch (IllegalArgumentException e) { return Page.empty(pageable); }
        if (mode == Course.Mode.EXCLUSIVO) {
            return Page.empty(pageable);
        }
        return toCardPage(cR.findByTypeAndMode(type, mode, pageable), favs);
    }

    // ----------------- Filtro por duración (excluye EXCLUSIVO) -----------------
    @Override
    public Page<CourseCardDTO> getCourseCardsByDurationRangeAndType(String range, String type, Pageable pageable) {
        Page<Course> courses = switch (range) {
            case "1-9"   -> cR.findByTypeAndDurationBetweenExcludingExclusive(type, Course.Mode.EXCLUSIVO, 1, 9, pageable);
            case "10-20" -> cR.findByTypeAndDurationBetweenExcludingExclusive(type, Course.Mode.EXCLUSIVO, 10, 20, pageable);
            case "+20"   -> cR.findByTypeAndDurationGteExcludingExclusive(type, Course.Mode.EXCLUSIVO, 21, pageable);
            default      -> Page.empty(pageable);
        };
        return toCardPage(courses, Collections.emptySet());
    }

    @Override
    public Page<CourseCardDTO> getCourseCardsByDurationRangeAndType(String range, String type, String clerkId, Pageable pageable) {
        Set<Long> favs = favoritesOf(clerkId);
        Page<Course> courses = switch (range) {
            case "1-9"   -> cR.findByTypeAndDurationBetweenExcludingExclusive(type, Course.Mode.EXCLUSIVO, 1, 9, pageable);
            case "10-20" -> cR.findByTypeAndDurationBetweenExcludingExclusive(type, Course.Mode.EXCLUSIVO, 10, 20, pageable);
            case "+20"   -> cR.findByTypeAndDurationGteExcludingExclusive(type, Course.Mode.EXCLUSIVO, 21, pageable);
            default      -> Page.empty(pageable);
        };
        return toCardPage(courses, favs);
    }

    // ----------------- Filtro por tags (excluye EXCLUSIVO) -----------------
    @Override
    public Page<CourseCardDTO> getCoursesByModuleTagsAndType(String type, List<Long> tagIds, Pageable pageable) {
        Page<Course> page = (tagIds == null || tagIds.isEmpty())
                ? cR.findByTypeAndModeNot(type, Course.Mode.EXCLUSIVO, pageable)
                : cR.findDistinctByTypeAndModulesTagsInExcludingExclusive(type, Course.Mode.EXCLUSIVO, tagIds, pageable);
        return toCardPage(page, Collections.emptySet());
    }

    @Override
    public Page<CourseCardDTO> getCoursesByModuleTagsAndType(String type, List<Long> tagIds, String clerkId, Pageable pageable) {
        Set<Long> favs = favoritesOf(clerkId);
        Page<Course> page = (tagIds == null || tagIds.isEmpty())
                ? cR.findByTypeAndModeNot(type, Course.Mode.EXCLUSIVO, pageable)
                : cR.findDistinctByTypeAndModulesTagsInExcludingExclusive(type, Course.Mode.EXCLUSIVO, tagIds, pageable);
        return toCardPage(page, favs);
    }

    // ----------------- ADMIN: solo EXCLUSIVO -----------------
    @Override
    public Page<CourseCardDTO> getExclusiveCourseCards(Pageable pageable) {
        return toCardPage(cR.findByMode(Course.Mode.EXCLUSIVO, pageable), Collections.emptySet());
    }

    @Override
    public Page<CourseCardDTO> getExclusiveCourseCards(String clerkId, Pageable pageable) {
        return toCardPage(cR.findByMode(Course.Mode.EXCLUSIVO, pageable), favoritesOf(clerkId));
    }
}


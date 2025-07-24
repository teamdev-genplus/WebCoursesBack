package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.services.ICourseService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;

@Service
public class CourseServiceImp implements ICourseService {
    @Autowired
    private ICourseRepo cR;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<Course> listAll() {
        return cR.findAll();
    }

    @Override
    public void delete(Long courseId) {
        cR.deleteById(courseId);
    }

    @Override
    public Page<CourseCardDTO> getAllCourseCards(Pageable pageable) {
        Page<Course> courses = cR.findAll(pageable);
        return courses.map(this::mapToCourseCardDTO);
    }

    @Override
    public Page<CourseCardDTO> getCourseCardsByType(String type, Pageable pageable) {
        Page<Course> coursesPage = cR.findByType(type, pageable);
        return coursesPage.map(this::mapToCourseCardDTO);
    }

    @Override
    public List<HighlightedCourseDTO> getAllHighlightedCourses() {
        List<Course> courses = cR.findByHighlightedTrueOrderByOrderNumberAsc();
        return courses.stream()
                .map(c -> new HighlightedCourseDTO(
                        c.getCourseId(),
                        c.getTitle(),
                        c.getDescription(),
                        c.getHighlightImage()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseCardDTO> findCoursesByTitle(String title) {
        List<Course> courses = cR.findByTitleIgnoreCaseContaining(title);
        return courses.stream()
                .map(this::mapToCourseCardDTO)
                .collect(Collectors.toList());
    }

    //Implementación del filtro por modalidad
    @Override
    public Page<CourseCardDTO> getCourseCardsByModeAndType(String modeStr, String type, Pageable pageable) {
        if (modeStr == null || modeStr.equalsIgnoreCase("TODOS")) {
            return cR.findByType(type, pageable).map(this::mapToCourseCardDTO);
        }

        Course.Mode mode;
        try {
            mode = Course.Mode.valueOf(modeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Page.empty(pageable);
        }

        return cR.findByTypeAndMode(mode, type, pageable).map(this::mapToCourseCardDTO);
    }

    @Override
    public Page<CourseCardDTO> getCourseCardsByDurationRangeAndType(String range, String type, Pageable pageable) {
        Page<Course> courses;

        switch (range) {
            case "1-4":
                courses = cR.findByTypeAndCantTotalHoursBetween(type, 1, 4, pageable);
                break;
            case "4-10":
                courses = cR.findByTypeAndCantTotalHoursBetween(type, 4, 10, pageable);
                break;
            case "10-20":
                courses = cR.findByTypeAndCantTotalHoursBetween(type, 10, 20, pageable);
                break;
            case "+20":
                courses = cR.findByTypeAndCantTotalHoursGreaterThanEqual(type, 20, pageable);
                break;
            default:
                courses = cR.findByType(type, pageable);
                break;
        }

        return courses.map(this::mapToCourseCardDTO);
    }

    @Override
    public Page<CourseCardDTO> getCoursesByModuleTagsAndType(String type, List<Long> tagIds, Pageable pageable) {
        if (tagIds == null || tagIds.isEmpty()) {
            return cR.findByType(type, pageable).map(this::mapToCourseCardDTO);
        }
        return cR.findDistinctByTypeAndModulesTagsIn(type, tagIds, pageable).map(this::mapToCourseCardDTO);
    }

    private CourseCardDTO mapToCourseCardDTO(Course c) {
        return new CourseCardDTO(
                c.getCourseId(),
                c.getPrincipalImage(),
                c.getTitle(),
                c.getOrderNumber(),
                c.getType(),
                c.getCantModOrHours(),
                c.getMode(),
                c.getUrlnamecourse()
        );
    }


}

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
        return courses.map(course -> {
            CourseCardDTO dto = new CourseCardDTO();
            dto.setCourseId(course.getCourseId());
            dto.setPrincipalImage(course.getPrincipalImage());
            dto.setTitle(course.getTitle());
            dto.setOrderNumber(course.getOrderNumber());
            dto.setType(course.getType());
            dto.setCantModOrHours(course.getCantModOrHours());
            dto.setMode(course.getMode());
            return dto;
        });
    }

    @Override
    public Page<CourseCardDTO> getCourseCardsByType(String type, Pageable pageable) {
        // Buscar cursos filtrados por tipo con paginación y orden
        Page<Course> coursesPage = cR.findByType(type, pageable);

        // Mapear entidades a DTOs
        return coursesPage.map(course -> {
            CourseCardDTO dto = new CourseCardDTO();
            dto.setCourseId(course.getCourseId());
            dto.setPrincipalImage(course.getPrincipalImage());
            dto.setTitle(course.getTitle());
            dto.setOrderNumber(course.getOrderNumber());
            dto.setType(course.getType());
            dto.setCantModOrHours(course.getCantModOrHours());
            dto.setMode(course.getMode());
            return dto;
        });
    }
}

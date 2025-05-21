package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.*;
import com.aecode.webcoursesback.repositories.*;
import com.aecode.webcoursesback.services.ICourseService;
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
    private ICourseRepo courseRepo;

    @Autowired
    private IUserCourseRepo userCourseAccessRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public void insert(Course course) {
        courseRepo.save(course);
    }

    @Override
    public List<Course> listAll() {
        return courseRepo.findAll();
    }

    @Override
    public void delete(int courseId) {
        courseRepo.deleteById(courseId);
    }

    @Override
    public Course getById(int courseId) {
        return courseRepo.findById(courseId).orElse(null);
    }

}

package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICourseService {
    void insert(Course course);
    List<Course> listAll();
    void delete(int courseId);
    Course getById(int courseId);
}

package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.CourseSummaryDTO;
import com.aecode.webcoursesback.dtos.CourseDetailDTO;
import com.aecode.webcoursesback.dtos.MyCourseDTO;
import com.aecode.webcoursesback.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICourseService {
    void insert(Course course);
    List<Course> listAll();
    void delete(int courseId);
    Course getById(int courseId);
    Page<CourseSummaryDTO> paginatedList(int offset, Pageable pageable);
    Page<CourseSummaryDTO> paginateByMode(String mode, Pageable pageable);
    Page<CourseSummaryDTO> listByTags(List<Integer> tagIds, Pageable pageable);
    List<Course> findCoursesByUserId(int userId);
    CourseDetailDTO getCourseDetail(int courseId);
    MyCourseDTO getMyCourse(int userId, int courseId);
    List<Course> searchByAttribute(String attribute, String value);
}

package com.aecode.webcoursesback.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aecode.webcoursesback.entities.CourseTag;

public interface ICourseTagRepo extends JpaRepository<CourseTag, Integer> {

}

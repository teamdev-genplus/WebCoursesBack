package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface ICourseRepo extends JpaRepository<Course,Integer>, JpaSpecificationExecutor<Course> {

}

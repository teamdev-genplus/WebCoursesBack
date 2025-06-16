package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IModuleRepo extends JpaRepository<Module,Long> {
    List<Module> findByCourse_CourseIdOrderByOrderNumberAsc(Long courseId);
}

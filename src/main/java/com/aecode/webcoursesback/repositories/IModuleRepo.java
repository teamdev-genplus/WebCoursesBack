package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IModuleRepo extends JpaRepository<Module,Integer> {
    @Query("SELECT m FROM Module m WHERE m.course.title LIKE %:courseTitle%")
    List<Module> findModulesByCourseTitle(@Param("courseTitle") String courseTitle);
}

package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUnitRepo extends JpaRepository<Unit,Integer> {
    @Query("SELECT u FROM Unit u WHERE u.module.course.title LIKE %:courseTitle%")
    List<Unit> findUnitsByCourseTitle(@Param("courseTitle") String courseTitle);
}

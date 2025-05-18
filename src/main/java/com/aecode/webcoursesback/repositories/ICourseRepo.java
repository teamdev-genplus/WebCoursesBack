package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aecode.webcoursesback.entities.Course.Mode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

@Repository
public interface ICourseRepo extends JpaRepository<Course,Integer>, JpaSpecificationExecutor<Course> {
    @Query(value = "SELECT * FROM courses ORDER BY order_number ASC", nativeQuery = true)
    List<Course> listByOrderNumber();

    @Query("SELECT c FROM Course c WHERE c.programTitle = :programTitle AND c.module = :module")
    Course findByModuleAndProgram(@Param("module") String module, @Param("programTitle") String programTitle);

    @Query(value = "SELECT * FROM get_courses_by_tag(CAST(:tagIds AS INTEGER[])) AS t ORDER BY t.order_number ASC", nativeQuery = true)
    Page<Course> findByCourseTags(@Param("tagIds") String tagIds, Pageable pageable);

    Page<Course> findByOrderNumberGreaterThan(int offset, Pageable pageable);

    Page<Course> findByMode(Mode mode, Pageable pageable);


}

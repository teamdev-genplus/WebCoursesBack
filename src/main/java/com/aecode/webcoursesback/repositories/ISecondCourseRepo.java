package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.SecondaryCourses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ISecondCourseRepo extends JpaRepository<SecondaryCourses, Long>, JpaSpecificationExecutor<SecondaryCourses> {

    @Query(value = "SELECT * FROM secondary_courses ORDER BY order_number ASC", nativeQuery = true)
    List<SecondaryCourses> listByOrderNumber();

    @Query("SELECT s FROM SecondaryCourses  s WHERE s.programTitle = :programTitle AND s.module = :module")
    SecondaryCourses findByModulexProgram(@Param("module") String module, @Param("programTitle") String programTitle);

    @Query(value = "SELECT * FROM get_courses_by_tag(CAST(:tagIds AS INTEGER[])) AS t ORDER BY t.order_number ASC", nativeQuery = true)
    Page<SecondaryCourses> findByCourseTags(@Param("tagIds") String tagIds, Pageable pageable);

    // Spring genera la consulta automáticamente
    Page<SecondaryCourses> findByOrderNumberGreaterThan(int offsetCourseId, Pageable pageable);

    // Spring genera la consulta automáticamente
    Page<SecondaryCourses> findByMode(SecondaryCourses.Mode mode, Pageable pageable);
}

package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.SecondaryCourses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ISecondCourseRepo extends JpaRepository<SecondaryCourses, Long> {

    @Query(value = "SELECT * FROM secondary_courses ORDER BY order_number ASC", nativeQuery = true)
    List<SecondaryCourses> listByOrderNumber();

    @Query("SELECT s FROM SecondaryCourses  s WHERE s.programTitle = :programTitle AND s.module = :module")
    SecondaryCourses findByModulexProgram(@Param("module") String module, @Param("programTitle") String programTitle);

    // Spring genera la consulta automáticamente
    Page<SecondaryCourses> findByOrderNumberGreaterThan(int offsetCourseId, Pageable pageable);

    // Spring genera la consulta automáticamente
    Page<SecondaryCourses> findByMode(String mode, Pageable pageable);
}

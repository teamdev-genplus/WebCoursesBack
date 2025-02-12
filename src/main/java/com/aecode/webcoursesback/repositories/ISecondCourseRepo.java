package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.SecondaryCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ISecondCourseRepo extends JpaRepository<SecondaryCourses, Integer> {

    @Query("SELECT s FROM SecondaryCourses  s WHERE s.programTitle = :programTitle AND s.module = :module")
    SecondaryCourses findByModulexProgram(@Param("module") String module, @Param("programTitle") String programTitle);
}

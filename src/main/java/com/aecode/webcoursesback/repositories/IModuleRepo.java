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
public interface IModuleRepo extends JpaRepository<Module,Integer> {
    @Query("SELECT m FROM Module m ORDER BY m.orderNumber ASC")
    List<Module> listByOrderNumber();

    @Query("SELECT m FROM Module m WHERE m.programTitle = :programTitle AND m.urlName = :urlName")
    Module findByProgramTitleAndUrlName(@Param("programTitle") String programTitle, @Param("urlName") String urlName);

    @Query("SELECT m FROM Module m JOIN m.tags t WHERE t.courseTagId IN :tagIds ORDER BY m.orderNumber ASC")
    Page<Module> findByTags(@Param("tagIds") List<Integer> tagIds, Pageable pageable);

    Page<Module> findByOrderNumberGreaterThan(int offsetOrderNumber, Pageable pageable);

    Page<Module> findByMode(Module.Mode mode, Pageable pageable);

    List<Module> findByTypeOrderByOrderNumberAsc(String type);
}

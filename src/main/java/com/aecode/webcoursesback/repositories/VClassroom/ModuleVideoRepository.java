package com.aecode.webcoursesback.repositories.VClassroom;
import com.aecode.webcoursesback.entities.VClassroom.ModuleVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleVideoRepository extends JpaRepository<ModuleVideo, Long> {
    List<ModuleVideo> findByModule_ModuleIdOrderByOrderNumberAsc(Long moduleId);
    Optional<ModuleVideo> findFirstByModule_ModuleIdOrderByOrderNumberAsc(Long moduleId);
}
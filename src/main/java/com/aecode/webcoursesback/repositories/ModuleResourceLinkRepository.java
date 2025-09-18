package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.ModuleResourceLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModuleResourceLinkRepository extends JpaRepository<ModuleResourceLink, Long> {

    // Recursos activos por MÓDULO (dropdown del perfil del módulo)
    @Query("""
           SELECT m FROM ModuleResourceLink m
           WHERE m.active = true AND m.module.moduleId = :moduleId
           ORDER BY m.orderNumber ASC
           """)
    List<ModuleResourceLink> findActiveByModuleIdOrderByOrderNumberAsc(Long moduleId);

    // Recursos activos por VIDEO (materiales de la vista de reproducción)
    @Query("""
           SELECT m FROM ModuleResourceLink m
           WHERE m.active = true AND m.video.id = :videoId
           ORDER BY m.orderNumber ASC
           """)
    List<ModuleResourceLink> findActiveByVideoIdOrderByOrderNumberAsc(Long videoId);
}

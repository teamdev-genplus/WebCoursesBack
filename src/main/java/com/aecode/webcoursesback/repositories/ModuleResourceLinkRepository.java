package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.ModuleResourceLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModuleResourceLinkRepository extends JpaRepository<ModuleResourceLink, Long> {

    // Solo activos, ordenados
    @Query("SELECT m FROM ModuleResourceLink m WHERE m.active = true AND m.module.moduleId = :moduleId ORDER BY m.orderNumber ASC")
    List<ModuleResourceLink> findActiveByModuleIdOrderByOrderNumberAsc(Long moduleId);
}

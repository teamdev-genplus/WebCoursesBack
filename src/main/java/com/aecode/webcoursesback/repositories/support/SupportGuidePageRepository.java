package com.aecode.webcoursesback.repositories.support;

import com.aecode.webcoursesback.entities.support.SupportGuidePage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupportGuidePageRepository extends JpaRepository<SupportGuidePage, Long> {
    Optional<SupportGuidePage> findBySlug(String slug);
    boolean existsBySlug(String slug);
}

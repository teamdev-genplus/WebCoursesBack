package com.aecode.webcoursesback.repositories.Landing;
import com.aecode.webcoursesback.entities.Landing.LandingPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LandingPageRepository extends JpaRepository<LandingPage, Long> {
    Optional<LandingPage> findBySlug(String slug);
}

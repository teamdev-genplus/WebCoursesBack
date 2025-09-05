package com.aecode.webcoursesback.repositories.Training;

import com.aecode.webcoursesback.entities.Training.Promotional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionalRepo extends JpaRepository<Promotional, Long> {
    // útil para la vista pública / home
    List<Promotional> findByActiveTrueOrderByIdDesc();
}

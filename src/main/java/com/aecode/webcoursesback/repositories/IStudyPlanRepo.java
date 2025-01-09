package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IStudyPlanRepo extends JpaRepository<StudyPlan, Integer> {
}

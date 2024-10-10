package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAnswerRepo extends JpaRepository<Answer,Integer> {
}

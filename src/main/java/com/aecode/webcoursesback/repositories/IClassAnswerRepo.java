package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.ClassAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClassAnswerRepo extends JpaRepository<ClassAnswer,Integer> {
}

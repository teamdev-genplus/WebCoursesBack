package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IQuestionRepo extends JpaRepository<Question,Integer> {
}

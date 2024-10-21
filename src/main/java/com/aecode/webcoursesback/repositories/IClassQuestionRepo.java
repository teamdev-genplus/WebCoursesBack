package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.SessionTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClassQuestionRepo extends JpaRepository<SessionTest,Integer> {
}

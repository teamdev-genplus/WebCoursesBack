package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserProgressRW;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserProgressRwRepo extends JpaRepository<UserProgressRW,Integer> {
}

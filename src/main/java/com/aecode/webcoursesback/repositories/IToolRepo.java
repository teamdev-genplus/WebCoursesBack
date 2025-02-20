package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IToolRepo extends JpaRepository<Tool, Integer> {
}

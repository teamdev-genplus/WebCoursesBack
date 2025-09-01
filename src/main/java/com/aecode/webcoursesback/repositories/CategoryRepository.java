package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> { }
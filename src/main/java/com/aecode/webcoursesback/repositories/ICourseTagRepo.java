package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICourseTagRepo extends JpaRepository<Tag, Integer> {

}

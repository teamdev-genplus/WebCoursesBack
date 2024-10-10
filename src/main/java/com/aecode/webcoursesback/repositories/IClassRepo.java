package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClassRepo extends JpaRepository<Class,Integer> {
}

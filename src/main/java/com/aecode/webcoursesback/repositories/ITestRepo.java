package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITestRepo extends JpaRepository<Test,Integer> {
}

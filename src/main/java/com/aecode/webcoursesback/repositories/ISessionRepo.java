package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISessionRepo extends JpaRepository<Session,Integer> {
}

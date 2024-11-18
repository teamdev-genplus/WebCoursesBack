package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserDetailRepo extends JpaRepository<UserDetail, Integer> {
}

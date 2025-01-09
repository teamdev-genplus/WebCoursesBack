package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserModuleAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserModuleRepo extends JpaRepository<UserModuleAccess, Integer> {
}

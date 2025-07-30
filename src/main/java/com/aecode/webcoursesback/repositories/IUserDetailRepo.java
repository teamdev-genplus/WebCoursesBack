package com.aecode.webcoursesback.repositories;

import com.aecode.webcoursesback.entities.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserDetailRepo extends JpaRepository<UserDetail, Integer> {
    @Query("SELECT u FROM UserDetail u WHERE u.userProfile.userId = ?1")
    UserDetail findByUserId(Long userId);

    //buscar user por clerkid de UserProfile
    @Query("SELECT u FROM UserDetail u WHERE u.userProfile.clerkId = ?1")
    UserDetail findByClerkId(String clerkId);


}

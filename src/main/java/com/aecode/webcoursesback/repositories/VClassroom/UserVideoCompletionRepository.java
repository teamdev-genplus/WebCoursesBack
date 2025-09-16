package com.aecode.webcoursesback.repositories.VClassroom;
import com.aecode.webcoursesback.entities.VClassroom.UserVideoCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserVideoCompletionRepository extends JpaRepository<UserVideoCompletion, Long> {
    Optional<UserVideoCompletion> findByUserProfile_ClerkIdAndVideo_Id(String clerkId, Long videoId);
    List<UserVideoCompletion> findByUserProfile_ClerkIdAndVideo_Module_ModuleId(String clerkId, Long moduleId);
}

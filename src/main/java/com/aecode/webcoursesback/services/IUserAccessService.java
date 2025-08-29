package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.dtos.Profile.ModuleProfileDTO;

import java.util.List;

public interface IUserAccessService {
    UserCourseDTO grantCourseAccess(String clerkId, Long courseId);
    UserModuleDTO  grantModuleAccess(String clerkId, Long moduleId);
    List<CourseCardProgressDTO> getAccessibleCoursesForUser(String clerkId);
    boolean hasAccessToModule(String clerkId, Long moduleId);

    ModuleProfileDTO getFirstAccessibleModuleForUser(String clerkId, Long courseId);
    ModuleProfileDTO getFirstAccessibleModuleForUserBySlug(String clerkId, String urlnamecourse); // <--- NUEVO


    List<UserCourseDTO> getAllCourses();
    List<UserModuleDTO> getAllModules();
    ModuleProfileDTO getModuleById(Long moduleId, String clerkId);
    boolean markModuleAsCompleted(String clerkId, Long moduleId);
    List<UserModuleDTO> grantMultipleModuleAccess(String clerkId, List<Long> moduleIds);
    List<UserModuleDTO> getUserModulesByClerkId(String clerkId);

}

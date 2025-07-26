package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.entities.UserCourseAccess;
import com.aecode.webcoursesback.entities.UserModuleAccess;

import java.util.List;

public interface IUserAccessService {
    UserCourseAccess grantCourseAccess(String clerkId, Long courseId);
    UserModuleAccess grantModuleAccess(String clerkId, Long moduleId);
    List<CourseCardProgressDTO> getAccessibleCoursesForUser(String clerkId);
    boolean hasAccessToModule(String clerkId, Long moduleId);
    ModuleDTO getFirstAccessibleModuleForUser(String clerkId, Long courseId);
    List<UserCourseDTO> getAllCourses();
    List<UserModuleDTO> getAllModules();
    ModuleDTO getModuleById(Long moduleId);
    boolean markModuleAsCompleted(String clerkId, Long moduleId);
    List<UserModuleAccess> grantMultipleModuleAccess(String clerkId, List<Long> moduleIds);
    List<UserModuleDTO> getUserModulesByClerkId(String clerkId);

}

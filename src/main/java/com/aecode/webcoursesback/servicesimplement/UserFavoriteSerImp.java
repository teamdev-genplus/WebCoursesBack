package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.CourseCardDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.UserFavorite;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.ICourseRepo;
import com.aecode.webcoursesback.repositories.IUserFavoriteRepo;
import com.aecode.webcoursesback.services.IUserFavoriteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserFavoriteSerImp implements IUserFavoriteService {

    @Autowired
    private IUserFavoriteRepo userFavoriteRepo;

    @Autowired
    private ICourseRepo courseRepo;

    @Override
    public void addFavorite(Long userId, Long courseId) {
        Optional<UserFavorite> existing = userFavoriteRepo.findByUserProfile_UserIdAndCourse_CourseId(userId, courseId);
        if (existing.isEmpty()) {
            UserProfile user = new UserProfile();
            user.setUserId(userId);
            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));
            UserFavorite favorite = UserFavorite.builder()
                    .userProfile(user)
                    .course(course)
                    .build();
            userFavoriteRepo.save(favorite);
        }
    }

    @Override
    public void removeFavorite(Long userId, Long courseId) {
        userFavoriteRepo.deleteByUserProfile_UserIdAndCourse_CourseId(userId, courseId);
    }

    @Override
    public List<Long> getFavoriteCourseIdsByUser(Long userId) {
        return userFavoriteRepo.findByUserProfile_UserId(userId)
                .stream()
                .map(fav -> fav.getCourse().getCourseId())
                .collect(Collectors.toList());
    }

    @Override
    public Page<CourseCardDTO> getFavoriteCoursesByUserAndType(Long userId, String type, Pageable pageable) {
        List<Long> favoriteCourseIds = getFavoriteCourseIdsByUser(userId);
        if (favoriteCourseIds.isEmpty()) {
            return Page.empty(pageable);
        }
        Page<Course> courses = courseRepo.findByCourseIdInAndType(favoriteCourseIds, type, pageable);
        return courses.map(course -> new CourseCardDTO(
                course.getCourseId(),
                course.getPrincipalImage(),
                course.getTitle(),
                course.getOrderNumber(),
                course.getType(),
                course.getCantModOrHours(),
                course.getMode(),
                course.getUrlnamecourse()
        ));
    }

}

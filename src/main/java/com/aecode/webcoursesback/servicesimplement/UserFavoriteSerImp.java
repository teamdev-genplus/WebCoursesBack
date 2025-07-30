package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.CourseCardDTO;
import com.aecode.webcoursesback.dtos.UserFavoriteDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.UserFavorite;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.ICourseRepo;
import com.aecode.webcoursesback.repositories.IUserFavoriteRepo;
import com.aecode.webcoursesback.services.IUserFavoriteService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public void addFavorite(String clerkId, Long courseId) {
        Optional<UserFavorite> existing = userFavoriteRepo.findByUserProfile_ClerkIdAndCourse_CourseId(clerkId, courseId);
        if (existing.isEmpty()) {
            UserProfile user = new UserProfile();
            user.setClerkId(clerkId);
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
    public void removeFavorite(String clerkId, Long courseId) {
        userFavoriteRepo.deleteByUserProfile_ClerkIdAndCourse_CourseId(clerkId, courseId);
    }

    @Override
    public List<Long> getFavoriteCourseIdsByUser(String clerkId) {
        return userFavoriteRepo.findByUserProfile_ClerkId(clerkId)
                .stream()
                .map(fav -> fav.getCourse().getCourseId())
                .collect(Collectors.toList());
    }

    @Override
    public Page<CourseCardDTO> getFavoriteCoursesByUserAndType(String clerkId, String type, Pageable pageable) {
        List<Long> favoriteCourseIds = getFavoriteCourseIdsByUser(clerkId);
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

    @Override
    public List<UserFavoriteDTO> getAllFavorites() {
        return userFavoriteRepo.findAll().stream()
                .map(fav -> UserFavoriteDTO.builder()
                        .favoriteId(fav.getFavoriteId())
                        .userId(fav.getUserProfile().getUserId())
                        .courseId(fav.getCourse().getCourseId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<UserFavoriteDTO> getFavoritesByClerkId(String clerkId) {
        return userFavoriteRepo.findByUserProfile_ClerkId(clerkId).stream()
                .map(fav -> UserFavoriteDTO.builder()
                        .favoriteId(fav.getFavoriteId())
                        .userId(fav.getUserProfile().getUserId())
                        .courseId(fav.getCourse().getCourseId())
                        .build())
                .collect(Collectors.toList());
    }



}

package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.dtos.CourseCardDTO;
import com.aecode.webcoursesback.dtos.UserFavoriteDTO;
import com.aecode.webcoursesback.entities.Course;
import com.aecode.webcoursesback.entities.UserFavorite;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.repositories.ICourseRepo;
import com.aecode.webcoursesback.repositories.IUserFavoriteRepo;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.IUserFavoriteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private IUserProfileRepository userProfileRepo;

    @Autowired
    private ICourseRepo courseRepo;

    @Override
    @Transactional
    public void addFavorite(String clerkId, Long courseId) {
        if (userFavoriteRepo.existsByClerkAndCourse(clerkId, courseId)) return;

        // ENTIDADES managed
        UserProfile user = userProfileRepo.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("UserProfile no encontrado por clerkId: " + clerkId));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course no encontrado id: " + courseId));

        userFavoriteRepo.save(UserFavorite.builder()
                .userProfile(user)
                .course(course)
                .build());
    }


    @Override
    @Transactional
    public void removeFavorite(String clerkId, Long courseId) {
        userFavoriteRepo.deleteByClerkAndCourse(clerkId, courseId);
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
        return courses.map(course -> CourseCardDTO.builder()
                .courseId(course.getCourseId())
                .principalImage(course.getPrincipalImage())
                .title(course.getTitle())
                .orderNumber(course.getOrderNumber())
                .type(course.getType())
                .cantModOrHours(course.getCantModOrHours())
                .mode(course.getMode())
                .urlnamecourse(course.getUrlnamecourse())
                .favorite(true)
                .build()
        );
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

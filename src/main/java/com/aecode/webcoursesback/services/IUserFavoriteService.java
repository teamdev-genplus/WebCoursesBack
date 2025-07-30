package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.CourseCardDTO;
import com.aecode.webcoursesback.dtos.UserFavoriteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserFavoriteService {

    void addFavorite(String clerkId, Long courseId);

    void removeFavorite(String clerkId, Long courseId);

    List<Long> getFavoriteCourseIdsByUser(String clerkId);

    Page<CourseCardDTO> getFavoriteCoursesByUserAndType(String clerkId, String type, Pageable pageable);

    List<UserFavoriteDTO> getAllFavorites();

    List<UserFavoriteDTO> getFavoritesByClerkId(String clerkId);
}

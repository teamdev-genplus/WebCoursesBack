package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.dtos.CourseCardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserFavoriteService {

    void addFavorite(Long userId, Long courseId);

    void removeFavorite(Long userId, Long courseId);

    List<Long> getFavoriteCourseIdsByUser(Long userId);

    Page<CourseCardDTO> getFavoriteCoursesByUserAndType(Long userId, String type, Pageable pageable);
}

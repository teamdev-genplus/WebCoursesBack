package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.CourseCartDTO;


import java.util.List;
public interface IShoppingCartService {
    List<CourseCartDTO> getCartByUser(String email);

    void addModuleToCart(Long userId, Long moduleId);

    void updateModuleSelection(Long userId, Long moduleId, boolean selected);

    void removeCartItemById(Long cartId);

    void removeAllModulesFromCourse(Long userId, Long courseId);
}

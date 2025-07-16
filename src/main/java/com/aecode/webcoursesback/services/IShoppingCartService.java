package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.CourseCartDTO;


import java.util.List;
public interface IShoppingCartService {
    List<CourseCartDTO> getCartByUser(String email);

    void addModuleToCart(String email, Long moduleId);

    void updateModuleSelection(String email, Long moduleId, boolean selected);

    void removeCartItemById(Long cartId);

    void removeAllModulesFromCourse(String email, Long courseId);
}

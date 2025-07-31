package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.CourseCartDTO;


import java.util.List;
public interface IShoppingCartService {
    List<CourseCartDTO> getCartByUser(String clerkId);

    void addModuleToCart(String clerkId, Long moduleId);

    void updateModuleSelection(String clerkId, Long moduleId, boolean selected);

    void removeCartItemById(Long cartId);

    void removeAllModulesFromCourse(String clerkId, Long courseId);

    void addModulesToCart(String clerkId, List<Long> moduleIds);

}

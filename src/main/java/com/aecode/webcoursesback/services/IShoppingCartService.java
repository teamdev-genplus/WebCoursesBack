package com.aecode.webcoursesback.services;
import com.aecode.webcoursesback.dtos.CourseCartDTO;


import java.util.List;
public interface IShoppingCartService {
    CourseCartDTO getCartByUser(Long userId);

    void addModuleToCart(Long userId, Long moduleId);

    void updateModuleSelection(Long userId, Long moduleId, boolean selected);

    void removeModuleFromCart(Long userId, Long moduleId);
}

package com.aecode.webcoursesback.repositories;
import com.aecode.webcoursesback.entities.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IShoppingCartRepo extends JpaRepository<ShoppingCart, Long> {
    List<ShoppingCart> findByUserProfile_UserId(Long userId);

    Optional<ShoppingCart> findByUserProfile_UserIdAndModule_ModuleId(Long userId, Long moduleId);

    void deleteByUserProfile_UserIdAndModule_ModuleId(Long userId, Long moduleId);
}

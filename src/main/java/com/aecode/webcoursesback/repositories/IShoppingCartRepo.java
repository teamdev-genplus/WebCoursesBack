package com.aecode.webcoursesback.repositories;
import com.aecode.webcoursesback.entities.ShoppingCart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IShoppingCartRepo extends JpaRepository<ShoppingCart, Long> {
    List<ShoppingCart> findByUserProfile_clerkId(String clerkId);

    Optional<ShoppingCart> findByUserProfile_ClerkIdAndModule_ModuleId(String clerkId, Long moduleId);

    @Transactional
    void deleteByUserProfile_ClerkIdAndModule_ModuleIdIn(String clerkId, List<Long> moduleIds);
}

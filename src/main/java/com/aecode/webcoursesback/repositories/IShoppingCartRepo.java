package com.aecode.webcoursesback.repositories;
import com.aecode.webcoursesback.entities.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IShoppingCartRepo extends JpaRepository<ShoppingCart, Integer> {
    List<ShoppingCart> findByUserProfileUserId(int userId);
}

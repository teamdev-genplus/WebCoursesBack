package com.aecode.webcoursesback.services;

import com.aecode.webcoursesback.entities.ShoppingCart;

import java.util.List;
public interface IShoppingCartService {
    void insert(ShoppingCart shoppingCart);

    List<ShoppingCart> list();

    void delete(int id);
}

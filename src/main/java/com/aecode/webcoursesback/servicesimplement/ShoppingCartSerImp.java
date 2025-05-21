package com.aecode.webcoursesback.servicesimplement;

import com.aecode.webcoursesback.entities.ShoppingCart;
import com.aecode.webcoursesback.repositories.IShoppingCartRepo;
import com.aecode.webcoursesback.services.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartSerImp implements IShoppingCartService{
    @Autowired
    private IShoppingCartRepo scR;

    @Override
    public void insert(ShoppingCart shoppingCart) {
        scR.save(shoppingCart);
    }

    @Override
    public List<ShoppingCart> list() {
        return scR.findAll();
    }

    @Override
    public void delete(int id) {
        scR.deleteById(id);
    }
}

package com.example.buyonline.repository;

import com.example.buyonline.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query(value = "DELETE FROM shopping_cart WHERE shopping_cart.id = ?1", nativeQuery = true)
    void chooseById(Integer id);
}
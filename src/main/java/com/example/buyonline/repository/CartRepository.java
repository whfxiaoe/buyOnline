package com.example.buyonline.repository;

import com.example.buyonline.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM shopping_cart WHERE user_id = ?1", nativeQuery = true)
    int delById(Integer id);
}
package com.example.buyonline.repository;

import com.example.buyonline.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(value = "SELECT * FROM `order` WHERE user_id = ?1", nativeQuery = true)
    Order chooseById(Integer id);

}

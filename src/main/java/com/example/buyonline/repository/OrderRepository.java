package com.example.buyonline.repository;

import com.example.buyonline.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface OrderRepository extends JpaRepository<Order, Integer> {

//    @Query(value = "SELECT * FROM person WHERE tel = ?1 AND password = ?2", nativeQuery = true)
//    Person chooseTP(String tel, String password);
//
//    @Query(value = "SELECT * FROM person WHERE tel = ?1", nativeQuery = true)
//    Person validateTel(String tel);
//
    @Query(value = "SELECT * FROM `order` WHERE id = ?1", nativeQuery = true)
    Order chooseById(Integer id);

}

package com.example.buyonline.repository;

import com.example.buyonline.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoodsRepository extends JpaRepository<Goods, Integer> {

    @Query(value = "SELECT * FROM goods WHERE goods_id = ?1", nativeQuery = true)
    Goods chooseById(Integer goods_id);
}

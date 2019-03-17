package com.example.buyonline.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@ToString
@Getter
@Setter
@Entity
@Table(name = "shopping_cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId;
    private Integer id;
    private Integer goodsId;
    private Integer goodsCount;                //库存总数
    private Integer cartState;                //状态
    private Date cartCreate;                  //创建时间

}

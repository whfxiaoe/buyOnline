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
@Table(name = "order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;
    private Integer id;
    private String orderInfo;
    private Double orderValue;                 //总价格
    private Date orderCreate;                  //订单创建时间
    private Date orderUpdate;                  //订单修改时间
    private Integer state;                     //状态


}

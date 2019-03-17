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
@Table(name = "goods")
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer goodsId;
    private String goodsName;
    private Double goodsPrice;                 //价格
    private Integer goodsTotal;                //库存总数
    private Date goodsCreate;                  //创建时间
    private Date goodsUpdate;                  //更新时间
    private Integer goodsState;                //状态

}

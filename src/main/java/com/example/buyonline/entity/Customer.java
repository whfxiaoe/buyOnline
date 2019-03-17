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
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;

    private String customerPassword;
    private String customerName;
    private Integer customerSex;
    private String customerTel;
    private Integer numberOrNot;                    //是否会员：0非会员；1会员
    private Date customerCreate;                    //客户创建时间
}

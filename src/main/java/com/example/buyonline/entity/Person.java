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
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String tel;                     //电话
    private String password;
    private String name;
    private Integer state;                  //状态：1启用；0禁用
    private Integer role;                   //角色：0普通用户，1管理员
    private Date gmtCreate;                  //创建日期

}

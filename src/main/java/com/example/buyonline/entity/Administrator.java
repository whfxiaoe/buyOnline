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
@Table(name = "administrator")
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer administratorId;

    private String administratorPassword;
    private String administratorName;
    private Date gmtCreate;                  //创建时间
}

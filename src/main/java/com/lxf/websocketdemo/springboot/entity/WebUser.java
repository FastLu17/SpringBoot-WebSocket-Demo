package com.lxf.websocketdemo.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 小66
 * @create 2019-06-27 15:35
 **/
@Data
@Entity
@Table(name = "web_user")
public class WebUser implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;
}

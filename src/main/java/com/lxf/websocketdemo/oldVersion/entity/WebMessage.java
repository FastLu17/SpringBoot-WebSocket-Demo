package com.lxf.websocketdemo.oldVersion.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author 小66
 * @create 2019-06-27 15:46
 **/
@Data
@Entity
@Table(name = "web_message")
public class WebMessage implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "content")
    private String content;
}

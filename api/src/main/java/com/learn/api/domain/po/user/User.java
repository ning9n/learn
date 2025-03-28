package com.learn.api.domain.po.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;
    /**
     * 头像
     */
    private String cover;


    /**
     * 简介
     */
    private String intro;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

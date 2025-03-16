package com.learn.api.domain.vo.user;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class SelfProfileVo {

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

}

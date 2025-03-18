package com.learn.auth.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    /**
     * id
     */
    private Long id;

    /**
     * 密码
     */
    private String password;

    /**
     * 盐
     */
    private String salt;

    /**
     * 身份证号
     */
    private String idNumber;


    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

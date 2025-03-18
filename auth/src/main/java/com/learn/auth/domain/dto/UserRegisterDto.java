package com.learn.auth.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
@Data
public class UserRegisterDto {
    @NotEmpty
    private String password; // 用户密码
    @NotEmpty
    private String phone;//手机号
    @NotEmpty
    private Integer code;//验证码
}

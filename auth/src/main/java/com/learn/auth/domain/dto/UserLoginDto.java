package com.learn.auth.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class UserLoginDto {
    private String password;
    //验证码
    private Integer code;
    //手机号
    @NotEmpty(message = "手机号不能为空")
    private String phone;


    public UserLoginDto(String password, Integer code, String phone) {
        this.password = password;
        this.code = code;
        this.phone = phone;
    }
}

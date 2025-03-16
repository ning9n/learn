package com.learn.api.domain.dto.user;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
@Data
public class UserRegisterDto {
    @NotEmpty
    private String username; // 用户名
    @NotEmpty
    private String password; // 用户密码
    private String email; // 用户邮箱
    @NotEmpty
    private String phone;//手机号
    @NotEmpty
    private String code;//验证码
}

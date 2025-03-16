package com.learn.user.controller;

import com.learn.api.domain.dto.user.UserRegisterDto;
import com.learn.common.response.Response;
import com.learn.user.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;
    /**
     * 注册
     * 通过手机号获取验证码，注册时需要设置用户名、密码
     * @param dto 注册信息
     * @return id
     */
    @PostMapping("/register")
    public Response<Long> register(@RequestBody UserRegisterDto dto){
        Long userId=registerService.register(dto);
        return Response.ok(userId);
    }

    /**
     * 检查手机号是否可用
     */
    @GetMapping("/checkPhone")
    public Response<Boolean> checkPhone(String phone){
        Boolean use=registerService.checkPhone(phone);
        return Response.ok(use);
    }
    /**
     * 检查用户名是否可用
     */
    @GetMapping("/checkUsername")
    public Response<Boolean> checkUsername(String username){
        Boolean use=registerService.checkUsername(username);
        return Response.ok(use);
    }
}

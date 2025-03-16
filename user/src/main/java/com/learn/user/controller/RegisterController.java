package com.learn.user.controller;

import com.learn.api.domain.dto.user.UserRegisterDto;
import com.learn.common.response.Response;
import com.learn.user.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;
    /**
     * 注册
     * @param dto 注册信息
     * @return id
     */
    @PostMapping("/register")
    public Response<Long> register(@RequestBody UserRegisterDto dto){
        Long userId=registerService.register(dto);
        return Response.ok(userId);
    }
}

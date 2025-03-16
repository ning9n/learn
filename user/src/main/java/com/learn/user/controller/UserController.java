package com.learn.user.controller;

import com.learn.api.domain.vo.user.SelfProfileVo;
import com.learn.common.response.Response;
import com.learn.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    /**
     * 获取个人基本信息
     */
    public Response<SelfProfileVo> getSelfBaseProfile(){
        SelfProfileVo vo=userService.getSelfBaseProfile();
        return Response.ok(vo);
    }
    /*
    其他功能：
    获取个人详细信息
    获取他人基本信息
    获取关注列表
    关注他人
     */
}

package com.learn.user.controller;

import com.learn.api.domain.vo.ProfileVo;
import com.learn.api.domain.vo.user.BaseProfileVo;
import com.learn.common.response.Response;
import com.learn.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    /**
     * 获取用户基本信息,可以获取自己，也可以获取他人
     * TODO 设置隐私权限
     */
    @GetMapping("/baseProfile")
    public Response<BaseProfileVo> getBaseProfile(Long id){
        BaseProfileVo vo=userService.getBaseProfile(id);
        return Response.ok(vo);
    }
    /**
    * 获取个人详细信息
     */
    @GetMapping("/profile")
    public Response<ProfileVo> getProfile(){
        ProfileVo vo=userService.getProfile();
        return Response.ok(vo);
    }

    public Response<List<BaseProfileVo>> getIdolList(){
        List<BaseProfileVo> list=userService.getIdolList();
        return Response.ok(list);
    }
    /*
    其他功能：
    获取关注列表
    关注他人
     */
}

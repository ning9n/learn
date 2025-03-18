package com.learn.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learn.auth.domain.dto.UserLoginDto;
import com.learn.auth.constant.AuthConstant;
import com.learn.auth.domain.po.User;
import com.learn.auth.mapper.UserMapper;
import com.learn.auth.service.CheckCodeService;
import com.learn.auth.service.LoginService;
import com.learn.common.exception.CodeErrorException;
import com.learn.common.exception.NoSuchUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserMapper loginMapper;
    private final CheckCodeService checkCodeService;
    @Override
    public SaTokenInfo login(UserLoginDto dto) {
        String phone = dto.getPhone();
        LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<User>()
                .select(User::getId,User::getPassword,User::getSalt)
                .eq(User::getPhone, phone);
        User user = loginMapper.selectOne(wrapper);
        //用户不存在的情况
        if(user==null){
            throw new NoSuchUserException("用户不存在");
        }
        //验证码登录
        Integer code=dto.getCode();
        if(code!=null){
            String key= AuthConstant.AUTH_LOGIN_CODE +dto.getPhone();
            if(!checkCodeService.checkCheckCode(key,code)){
                throw new CodeErrorException("用户使用无效验证码登录");
            }
        }
        //密码登录
        else {
            String password = dto.getPassword();
            String salt= user.getSalt();
            if(!BCrypt.checkpw(password+salt,user.getPassword())){
                throw new NoSuchUserException("用户不存在或密码错误");
            }

        }
        //登录成功

        //StpUtil.login(userId, SystemConstants.LOGIN_USER_TTL);
        // `SaLoginModel`为登录参数Model，其有诸多参数决定登录时的各种逻辑，例如：
        StpUtil.login(user.getId(), new SaLoginModel()
                .setDevice("PC")                // 此次登录的客户端设备类型, 用于[同端互斥登录]时指定此次登录的设备类型
                .setIsLastingCookie(true)        // 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
                .setTimeout(60 * 60 * 24 * 7)    // 指定此次登录token的有效期, 单位:秒 （如未指定，自动取全局配置的 timeout 值）
                //     .setToken("xxxx-xxxx-xxxx-xxxx") // 预定此次登录的生成的Token
                .setIsWriteHeader(false)         // 是否在登录后将 Token 写入到响应头
        );
        SaTokenInfo tokenInfo=StpUtil.getTokenInfo();
        log.info("用户登录成功，{}",tokenInfo);
        return tokenInfo;
    }
}

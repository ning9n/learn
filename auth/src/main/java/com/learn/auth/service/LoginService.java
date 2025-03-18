package com.learn.auth.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.learn.auth.domain.dto.UserLoginDto;

public interface LoginService {
    /**
     * 目前只支持手机号+验证码/密码方式
     *
     * @return token
     */
    SaTokenInfo login(UserLoginDto userLoginDto);

}

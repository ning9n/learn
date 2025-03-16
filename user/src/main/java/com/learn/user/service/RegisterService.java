package com.learn.user.service;

import com.learn.api.domain.dto.user.UserRegisterDto;

public interface RegisterService {
    /**
     * 注册
     * @param dto 注册信息
     * @return id
     */
    Long register(UserRegisterDto dto);
}

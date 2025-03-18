package com.learn.auth.service;


import com.learn.auth.domain.dto.UserRegisterDto;

public interface RegisterService {
    /**
     * 注册
     * @param dto 注册信息
     * @return id
     */
    Long register(UserRegisterDto dto);

    Boolean checkPhone(String phone);


}

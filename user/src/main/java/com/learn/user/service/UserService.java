package com.learn.user.service;

import com.learn.api.domain.vo.ProfileVo;
import com.learn.api.domain.vo.user.BaseProfileVo;

import java.util.List;

public interface UserService {
    BaseProfileVo getBaseProfile(Long id);

    ProfileVo getProfile();

    List<BaseProfileVo> getIdolList();
}

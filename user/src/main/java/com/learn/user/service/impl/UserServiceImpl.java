package com.learn.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learn.api.domain.po.user.User;
import com.learn.api.domain.vo.user.SelfProfileVo;
import com.learn.user.mapper.UserMapper;
import com.learn.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Override
    public SelfProfileVo getSelfBaseProfile() {
        Object id = StpUtil.getLoginId();
        LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<>();
        wrapper.select(User::getUsername,User::getCover,User::getIntro)
                        .eq(User::getId,id);
        User user = userMapper.selectOne(wrapper);
        return BeanUtil.copyProperties(user, SelfProfileVo.class);
    }
}

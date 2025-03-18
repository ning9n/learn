package com.learn.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.learn.auth.domain.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}

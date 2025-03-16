package com.learn.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learn.api.domain.po.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

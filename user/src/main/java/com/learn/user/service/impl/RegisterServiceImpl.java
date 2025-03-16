package com.learn.user.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.learn.api.domain.dto.user.UserRegisterDto;
import com.learn.api.domain.po.user.User;
import com.learn.common.exception.CodeErrorException;
import com.learn.common.exception.UnknownException;
import com.learn.user.mapper.UserMapper;
import com.learn.user.service.RegisterService;
import com.learn.user.utils.RedisIdWorker;
import com.learn.user.utils.SystemConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterServiceImpl implements RegisterService {
    private final StringRedisTemplate redisTemplate;
    private final RedisIdWorker idWorker;
    private final UserMapper userMapper;

    /**
     * 注册
     * -
     * 优化：
     * 1.密码非对称加密传输
     * 2.加盐后使用哈希算法BCrypt加密
     * TODO 使用leaf生成id 密码非对称加密传输
     *
     * @param dto 注册信息
     * @return id
     */
    @Override
    public Long register(UserRegisterDto dto) {
        //校验验证码
        String code=dto.getCode();
        if(code!=null){
            String string = redisTemplate.opsForValue().get(SystemConstants.REGISTER_CODE_KEY + dto.getPhone());
            if(!code.equals(string)){
                throw new CodeErrorException("用户使用无效验证码注册");
            }
        }
        //注册
        //使用redis生成分布式id，时间戳+序列号
        // long id = segmentService.getId(SystemConstants.USER_REGISTER_KEY).getId();
        Long id= idWorker.nextId(SystemConstants.USER_REGISTER_KEY);
        User user = new User();
        user.setId(id);
        user.setUsername(dto.getUsername());
        //密码加盐并使用BCrypt加密
        String salt = BCrypt.gensalt(10);
        String password = BCrypt.hashpw(dto.getPassword() + salt);
        //设置其他数据
        user.setPassword(password);
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setSalt(salt);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        //插入数据库
        if(userMapper.insert(user)==1){
            log.info("用户注册成功，{}",id);
            return id;
        }
        throw new UnknownException("用户注册发生了未知异常");
    }
}

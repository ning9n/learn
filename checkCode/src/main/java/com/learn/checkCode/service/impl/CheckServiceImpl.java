package com.learn.checkCode.service.impl;

import cn.hutool.core.util.RandomUtil;

import com.learn.api.domain.dto.checkCode.GetCheckCodeDto;
import com.learn.checkCode.service.CheckCodeService;
import com.learn.common.exception.InvalidCheckCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckServiceImpl implements CheckCodeService {
    private final RedisTemplate<String,Integer> redisTemplate;
    //TODO 真实验证码发送(+短时间内重复发送检查)
    @Override
    public Boolean getCheckCode(GetCheckCodeDto getCheckCodeDto) {
        int checkCode = RandomUtil.randomInt(999999);
        HashMap<String,Integer> map=new HashMap<>(2);
        map.put("checkCode", checkCode);
        map.put("retryCount",getCheckCodeDto.getRetryCount());
        redisTemplate.opsForHash().putAll(getCheckCodeDto.getKey(),map);
        redisTemplate.expire(getCheckCodeDto.getKey(),getCheckCodeDto.getTime(),getCheckCodeDto.getTimeUnit());
        log.info("验证码："+checkCode);
        return true;
    }
    /*
    情况分类：过期、失败、成功

    其他逻辑：
    1.校验后验证码失效。
    2.校验失败后增加错误次数限制。
     */
    @Override
    public Boolean checkCheckCode(String key, Integer code) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        //验证码已删除
        if(map.isEmpty()){
            throw new InvalidCheckCodeException("无效验证码");
        }
        //减少重试次数
        redisTemplate.opsForHash().increment(key,"retryCount",-1);
        //验证码重试次数用完
        if((Integer) map.get("retryCount")<0){
            throw new InvalidCheckCodeException("无效验证码");
        }
        //验证码错误
        Integer correctCode = (Integer) map.get("checkCode");
        if(!code.equals(correctCode)){
            throw new InvalidCheckCodeException("无效验证码");
        }
        //删除验证码
        redisTemplate.delete(key);
        return true;
    }
}

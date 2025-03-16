package com.learn.checkCode.service;

import com.learn.api.domain.dto.checkCode.GetCheckCodeDto;

import java.util.concurrent.TimeUnit;

public interface CheckCodeService {


    Boolean getCheckCode(GetCheckCodeDto getCheckCodeDto);
    Boolean checkCheckCode(String key, Integer code);
}

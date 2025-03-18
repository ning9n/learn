package com.learn.auth.service;

import com.learn.auth.domain.dto.GetCheckCodeDto;

public interface CheckCodeService {


    Boolean getCheckCode(GetCheckCodeDto getCheckCodeDto);
    Boolean checkCheckCode(String key, Integer code);
}

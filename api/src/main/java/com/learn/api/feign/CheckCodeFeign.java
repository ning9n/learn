package com.learn.api.feign;

import com.learn.api.domain.dto.checkCode.GetCheckCodeDto;
import com.learn.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

@FeignClient("checkCode")
public interface CheckCodeFeign {

    @PostMapping("/getCheckCode")
    public Response<Boolean> getCheckCode(@RequestBody GetCheckCodeDto getCheckCodeDto);
    @PostMapping("/check")
    public Response<Boolean> checkCheckCode(@RequestParam String key, @RequestParam Integer code);

}

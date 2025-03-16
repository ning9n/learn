package com.learn.api.client;

import com.learn.api.domain.dto.checkCode.GetCheckCodeDto;
import com.learn.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("checkCode")
public interface CheckCodeClient {

    @PostMapping("/checkCode/getCheckCode")
    public Response<Boolean> getCheckCode(@RequestBody GetCheckCodeDto getCheckCodeDto);
    @PostMapping("/checkCode/check")
    public Response<Boolean> checkCheckCode(@RequestParam("key") String key, @RequestParam("code") Integer code);

}

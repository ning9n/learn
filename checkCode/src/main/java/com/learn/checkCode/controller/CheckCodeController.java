package com.learn.checkCode.controller;

import com.learn.api.domain.dto.checkCode.GetCheckCodeDto;
import com.learn.checkCode.service.CheckCodeService;
import com.learn.common.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
@Api(value = "验证码相关接口",tags = "验证码相关接口")
@RestController
@RequiredArgsConstructor
public class CheckCodeController {
    private final CheckCodeService checkCodeService;

    /**
     * 获取验证码
     * @return 是否成功
     */
    /*
    @Validated 在类级别
    @Valid 在方法参数级别
     */
    @PostMapping("/getCheckCode")
    @ApiOperation("获取验证码")
    public Response<Boolean> getCheckCode(@Valid @RequestBody GetCheckCodeDto getCheckCodeDto){
        Boolean ans=checkCodeService.getCheckCode(getCheckCodeDto);
        return Response.ok(ans);
    }

    /**
     * 检验验证码
     * @param key key
     * @param code 验证码
     * @return 是否成功
     */
    @PostMapping("/check")
    @ApiOperation("校验验证码")
    public Response<Boolean> checkCheckCode(@RequestParam String key,@RequestParam Integer code){
        Boolean ans=checkCodeService.checkCheckCode(key,code);
        return Response.ok(ans);
    }

}

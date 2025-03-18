package com.learn.auth.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.concurrent.TimeUnit;

@Data
public class GetCheckCodeDto {
    @NotNull(message = "Key 不能为空")
    //key
    private String key;
    //有效期时间长度
    @NotNull(message = "Time 不能为空")
    @Positive(message = "Time 必须是正数")
    private Long time;
    //有效期时间单位
    @NotNull(message = "TimeUnit 不能为空")
    private TimeUnit timeUnit;
    //重试次数
    @NotNull(message = "RetryCount 不能为空")
    @Positive(message = "RetryCount 必须是正数")
    private Integer retryCount;
}

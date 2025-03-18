package com.learn.api.domain.po.production;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class IncompleteProduction {
    //视频id
    private Long id;
    //用户id
    private Long userId;
    //上传成功的分片数
    private Integer part;
    //总分片数
    private Integer total;
    private LocalDateTime createTime;
}

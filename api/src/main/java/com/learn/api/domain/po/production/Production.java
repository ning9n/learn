package com.learn.api.domain.po.production;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Production {
    private Long id;
    //时长
    private String length;
    //minio路径
    private String url;
    //封面
    private String cover;
    //名字
    private String name;
    //简介
    private String intro;
    //用户id
    private Long userId;
    private Integer status;
    //创建时间
    private LocalDateTime createTime;
}

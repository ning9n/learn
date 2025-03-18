package com.learn.api.domain.dto.production;

import io.swagger.annotations.ApiModelProperty;

public class UploadVideoDto {
    @ApiModelProperty("视频路径")
    private String url;
    @ApiModelProperty("视频名称")
    private String name;
    @ApiModelProperty("视频介绍")
    private String intro;
    @ApiModelProperty("作者id")
    private int userId;
    @ApiModelProperty("视频封面")
    private String videoCover;
}

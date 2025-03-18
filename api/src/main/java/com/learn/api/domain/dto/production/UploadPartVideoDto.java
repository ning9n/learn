package com.learn.api.domain.dto.production;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class UploadPartVideoDto {
    //文件
    @NotNull
    private MultipartFile file;
    private Long id;
    //第几片 从0开始
    @NotNull
    private Integer part;
    //分片总数
    @NotNull
    private Integer total;
}

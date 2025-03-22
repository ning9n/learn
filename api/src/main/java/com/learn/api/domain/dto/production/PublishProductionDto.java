package com.learn.api.domain.dto.production;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PublishProductionDto {
    @NotNull
    private Long id;
    @NotEmpty
    //名字
    private String name;
    @NotNull
    //简介
    private String intro;

}

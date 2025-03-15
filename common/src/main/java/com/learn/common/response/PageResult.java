package com.learn.common.response;

import lombok.Data;

import java.util.List;
@Data
public class PageResult <T>{
    //分页数据
    private List<T> data;
    //总记录数
    private Long counts;
    private Integer pageNo;
    private Integer pageSize;
}

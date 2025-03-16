package com.learn.api.domain.vo.user;

import lombok.Data;

@Data
public class BaseProfileVo {
    //用户名
    private String username;
    //头像
    private String cover;
    //简介
    private String intro;

}

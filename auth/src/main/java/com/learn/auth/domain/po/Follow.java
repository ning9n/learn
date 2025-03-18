package com.learn.auth.domain.po;

import java.time.LocalDateTime;

public class Follow {
    //关注ID，主键
    private int id;
    //粉丝ID
    private int fansId;
    //被关注者ID
    private int idolId;
    private LocalDateTime createTime;

}

use user;
##用户表
CREATE TABLE `user`
(
    `id`          bigint      NOT NULL COMMENT 'id',
    `username`    varchar(20) NOT NULL COMMENT '用户名',
    `password`    varchar(100) NOT NULL COMMENT '密码',
    `salt`        varchar(100) NOT NULL COMMENT '盐',
    `cover`       varchar(100) DEFAULT NULL COMMENT '头像',
    `id_number`   varchar(18)  DEFAULT NULL COMMENT '身份证号',
    `intro`       varchar(100) DEFAULT NULL COMMENT '简介',
    `phone`       varchar(20)  NOT NULL COMMENT '手机号',
    `email`       varchar(50)  DEFAULT NULL COMMENT '邮箱',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ;

## 关注表
CREATE TABLE `follow`
(
    `id`          bigint NOT NULL COMMENT 'id',
    `fans_id`     bigint      NOT NULL  COMMENT '粉丝id',
    `idol_id`     bigint      NOT NULL  COMMENT '被关注者id',
    `create_time` datetime NOT NULL COMMENT '关注的创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_fans_idol` (`fans_id`, `idol_id`),
    INDEX `idx_idol_fans` (`idol_id`, `fans_id`)
)

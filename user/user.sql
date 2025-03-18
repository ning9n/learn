use user;
##用户表
CREATE TABLE `user`
(
    `id`          bigint      NOT NULL COMMENT 'id',
    `username`    varchar(20) NOT NULL COMMENT '用户名',
    `cover`       varchar(100) DEFAULT NULL COMMENT '头像',
    `intro`       varchar(100) DEFAULT NULL COMMENT '简介',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) COMMENT '保存用户在这个程序独有的信息，id与用户中心id相同';

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

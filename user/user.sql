use user;
##用户表
CREATE TABLE `user`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `username`    varchar(20) NOT NULL COMMENT '用户名',
    `password`    varchar(100) DEFAULT NULL COMMENT '密码',
    `salt`        varchar(10)  DEFAULT NULL COMMENT '盐',
    `cover`       varchar(100) DEFAULT NULL COMMENT '头像',
    `id_number`   varchar(18)  DEFAULT NULL COMMENT '身份证号',
    `intro`       varchar(100) DEFAULT NULL COMMENT '简介',
    `phone`       varchar(20)  DEFAULT NULL COMMENT '手机号',
    `email`       varchar(50)  DEFAULT NULL COMMENT '邮箱',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

## 关注表
CREATE TABLE `follow`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `fans_id`     int      DEFAULT NULL, #粉丝id
    `idol_id`     int      DEFAULT NULL, #被关注者id
    `create_time` datetime DEFAULT NULL, #关注的创建时间
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


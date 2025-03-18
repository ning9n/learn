CREATE TABLE `user`
(
    `id`          bigint      NOT NULL COMMENT 'id',
    `password`    varchar(100) NOT NULL COMMENT '密码',
    `salt`        varchar(100) NOT NULL COMMENT '盐',
    `id_number`   varchar(18)  DEFAULT NULL COMMENT '身份证号',
    `phone`       varchar(20)  NOT NULL COMMENT '手机号',
    `email`       varchar(50)  DEFAULT NULL COMMENT '邮箱',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) COMMENT '用户统一身份认证中心，保存用户基本信息';
## 作品表
CREATE TABLE `production`
(
    `id`          bigint NOT NULL COMMENT 'id',
    `length`      varchar(50)  DEFAULT NULL COMMENT '视频时长',
    `url`         varchar(100) NOT NULL COMMENT '视频在minio中存储路径',
    `cover`       varchar(100) DEFAULT NULL COMMENT '视频封面在minio中存储路径',
    `name`        varchar(200) DEFAULT NULL COMMENT '视频名称',
    `intro`       varchar(200) DEFAULT NULL COMMENT '视频介绍',
    `user_id`     bigint          DEFAULT NULL COMMENT '作者id',
    `status`      int DEFAULT 0 COMMENT '作品状态，0：上传中，1：合并中，2.解析中，3.待发布，4：审核中，5：发布成功，6.已下架',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
## 作品数据表
CREATE TABLE `production_data`
(
    `id`            bigint NOT NULL COMMENT 'id',
    `production_id`      int NOT NULL COMMENT '视频id',
    `comment_count` int DEFAULT 0 COMMENT '评论数',
    `play_count`    int DEFAULT 0 COMMENT '播放量',
    `barrage_count` int DEFAULT 0 COMMENT '弹幕量',
    `collect_count` int DEFAULT 0 COMMENT '收藏量',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`production_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

## 上传未完成的作品表
CREATE TABLE `incomplete_production`
(
    `id`   bigint NOT NULL COMMENT '作品id',
    `user_id` bigint NOT NULL COMMENT '用户id',
    `part` int NOT NULL COMMENT '上传成功的分片数',
    `total` int NOT NULL COMMENT '总分片数',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX (`user_id`)
)    ENGINE = InnoDB
     DEFAULT CHARSET = utf8mb4
     COLLATE = utf8mb4_0900_ai_ci;

## 视频、评论点赞表
CREATE TABLE `likes`
(
    `id`          int NOT NULL ,
    `video_id`    int      DEFAULT NULL,
    `user_id`     int      DEFAULT NULL,
    `comment_id`  int      DEFAULT NULL COMMENT '点赞的评论id，若点赞的是视频则该值为null',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
## 评论表
CREATE TABLE `comment`
(
    `id`          int NOT NULL ,
    `video_id`    int           DEFAULT NULL,
    `user_id`     int           DEFAULT NULL,
    `parent_id`   int           DEFAULT NULL COMMENT '父级评论id', #
    `top_id`      int           DEFAULT NULL COMMENT '顶层评论id',
    `content`     varchar(1000) DEFAULT NULL,
    `create_time` datetime      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
## 弹幕表
CREATE TABLE `danmaku`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `video_id`    int           DEFAULT NULL, #所在视频id
    `user_id`     int           DEFAULT NULL, #发表弹幕的用户id
    `content`     varchar(1000) DEFAULT NULL, #弹幕内容
    `create_time` datetime      NOT NULL, #弹幕创建时间
    `place`       int           DEFAULT NULL, #弹幕所处视频中的位置
    `type`        int           DEFAULT NULL, #弹幕显现方式，由左至右还是直接显现
    `color`       int           DEFAULT NULL, #弹幕颜色
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
## 评论消息表
CREATE TABLE `comment_notice`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `sender_id`   int      DEFAULT NULL, #评论消息的发送者id
    `receiver_id` int      DEFAULT NULL, #评论消息的接收者id
    `create_time` datetime DEFAULT NULL, #评论消息的创建时间
    `video_id`    int      DEFAULT NULL, #评论所在的视频id
    `status`      int      DEFAULT NULL, #消息已读状态
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
## 收藏表
CREATE TABLE `collect`
(
    `id`               int NOT NULL AUTO_INCREMENT,
    `video_id`         int      DEFAULT NULL, #收藏的视频id
    `collect_group_id` int      DEFAULT NULL, #所属收藏夹id
    `create_time`      datetime DEFAULT NULL, #收藏的创建时间
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
## 收藏夹表
CREATE TABLE `collect_group`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `name`        varchar(100) DEFAULT NULL, #收藏夹名称
    `user_id`     int          DEFAULT NULL, #收藏夹所属用户id
    `create_time` datetime     DEFAULT NULL, #收藏夹创建时间
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
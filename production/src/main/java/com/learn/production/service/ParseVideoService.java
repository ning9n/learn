package com.learn.production.service;

import org.springframework.scheduling.annotation.Async;

import java.io.IOException;

/**
 * 解析视频，获取时长和封面
 */
public interface ParseVideoService {
    /**
     * 解析视频，获取时长和封面
     * 只需要第一帧即可尝试解析
     * -
     * 三种情况：
     * 第一帧解析成功
     * 第一帧解析失败，合并后解析
     * 合并后第一帧未解析完，且第一帧解析失败，需要再次调用解析
     *
     * @param id    作品id
     * @param part  是不是分片文件
     */
    @Async("parseThreadPool")
    void parseVideo(Long id, boolean part) throws IOException;
}

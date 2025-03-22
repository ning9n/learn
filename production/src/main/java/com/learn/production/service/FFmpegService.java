package com.learn.production.service;

import java.io.IOException;

public interface FFmpegService {
    /**
     * 获取视频时长
     *
     * @param videoPath 视频路径
     * @return 时长
     */
    String getVideoDuration(String videoPath) throws IOException, InterruptedException;

    /**
     * 获取视频封面
     * @param videoPath 视频路径
     * @param thumbnailPath 封面路径
     * @throws IOException 获取失败
     */
    void extractVideoThumbnail(String videoPath, String thumbnailPath) throws IOException;
}

package com.learn.production.service.impl;

import com.learn.production.service.UrlService;
import com.learn.production.utils.RedisIdWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final RedisIdWorker idWorker;
    @Value("${ffmpeg.temporary-path}")
    private String temporaryPathPrefix;
     @Override
    public String getMinioUrlById(Long id, Integer part) {
        //根据id获取minio存放路径
        LocalDateTime now = idWorker.getCreateTime(id);
        //路径为年/月/日/id/chunk/part
        return now.getYear() + "/" + now.getMonth() + "/" + now.getDayOfMonth() + "/" + id + "/chunk/" + part;
    }

    @Override
    public String getMinioUrlById(Long id) {
        //根据id获取minio存放路径
        LocalDateTime now = idWorker.getCreateTime(id);
        //路径为年/月/日/id/chunk/part
        return now.getYear() + "/" + now.getMonth() + "/" + now.getDayOfMonth() + "/" + id + "/video";
    }

    @Override
    public String getMinioThumbnailUrlById(Long id) {
        //根据id获取minio存放路径
        LocalDateTime now = idWorker.getCreateTime(id);
        //路径为年/月/日/id/chunk/part
        return now.getYear() + "/" + now.getMonth() + "/" + now.getDayOfMonth() + "/" + id + "/thumbnail";
    }

    @Override
    public String getLocalThumbnailUrlById(Long id) {
        return temporaryPathPrefix + id+"\\thumbnail.png";
    }
    @Override
    public String getLocalUrlById(Long id) {
        return temporaryPathPrefix + id + "\\video.mp4";
    }

    @Override
    public String getLocalUrlById(Long id, Integer part) {
        return temporaryPathPrefix + id + "\\chunk\\" + part;
    }


}

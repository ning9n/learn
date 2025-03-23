package com.learn.production.service;

/**
 * 根据id获取指定存放路径
 */
public interface UrlService {
    String getLocalUrlById(Long id);

    String getLocalUrlById(Long id, Integer part);

    String getMinioUrlById(Long id, Integer part);

    String getMinioUrlById(Long id);

    String getMinioThumbnailUrlById(Long id);

    String getLocalThumbnailUrlById(Long id);
}

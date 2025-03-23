package com.learn.production.service;

import io.minio.errors.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 封装对minio的操作
 */
public interface MinioServer {
    public void uploadFile(String url, InputStream inputStream, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    void uploadLocalFile(String filename, String localFireName, String contentType) throws IOException;

    void compose(Long id, @NotNull Integer total) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**
     * 下载文件到本地
     * @param filename 文件名
     * @param  temporaryPath 本地路径
     */
    void downloadVideoFileToLocal(String filename, String temporaryPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**
     * 获取文件流
     * @param url 文件存放路径
     * @return 文件
     */
    InputStream downloadVideoFile(String url) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

}

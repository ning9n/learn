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
    public void uploadVideoFile(String url, InputStream inputStream, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    void compose(String url, @NotNull Integer total) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}

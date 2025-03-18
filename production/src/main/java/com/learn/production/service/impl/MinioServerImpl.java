package com.learn.production.service.impl;

import com.learn.production.service.MinioServer;
import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 封装对minio的操作
 */
@Service
@RequiredArgsConstructor
public class MinioServerImpl implements MinioServer {
    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public void uploadVideoFile(String filename, InputStream inputStream, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        PutObjectArgs args= PutObjectArgs.builder()
                .bucket(bucket)
                .stream(inputStream,-1,10*1024*1024)
                .object(filename)
                .contentType(contentType)
                .build();
        minioClient.putObject(args);
    }

    @Override
    public void compose(String url, @NotNull Integer total) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<ComposeSource> list= Stream.iterate(0, i -> ++i)
                .limit(total)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket)
                        .object((url+"/chunk/").concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        ComposeObjectArgs args= ComposeObjectArgs.builder()
                .bucket(bucket)
                .object(url)
                .sources(list)
                .build();
        minioClient.composeObject(args);
    }
}

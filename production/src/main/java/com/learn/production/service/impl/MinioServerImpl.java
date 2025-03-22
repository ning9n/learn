package com.learn.production.service.impl;

import com.learn.production.service.MinioServer;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.FileOutputStream;
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
@Slf4j
public class MinioServerImpl implements MinioServer {
    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucket;

    /**
     * 上传文件
     * @param filename 文件名
     * @param inputStream 文件
     * @param contentType 文件类型
     */
    @Override
    public void uploadVideoFile(String filename, InputStream inputStream, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        PutObjectArgs args= PutObjectArgs.builder()
                .bucket(bucket)
                .stream(inputStream,-1,10*1024*1024)
                .object(filename)
                .contentType(contentType)
                .build();
        minioClient.putObject(args);
        log.info("视频上传成功");
    }

    /**
     * 合并文件
     * @param url 合并后的文件存放路径
     * @param total 总片数
     */
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
        log.info("视频合并成功");
    }

    /**
     * 下载文件到本地
     * @param filename 文件名
     * @param temporaryPath 本地路径
     */
    @Override
    @Async("downloadThreadPool")
    public void downloadVideoFileToLocal(String filename, String temporaryPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try (InputStream inputStream = downloadVideoFile(filename)) {
            try (FileOutputStream outputStream = new FileOutputStream(temporaryPath)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            }
        }
    }

    /**
     * 获取文件流
     * @param url 文件存放路径
     * @return 文件
     */
    @Override
    public InputStream downloadVideoFile(String url) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectArgs args=GetObjectArgs.builder()
                .bucket(bucket)
                .object(url)
                .build();
        return minioClient.getObject(args);
    }

    /**
     * 把本地文件保存到minio
     * @param localThumbnailPath 本地路径
     * @param thumbnailPath minio路径
     */
    @Override
    public void uploadLocalFile(String localThumbnailPath, String thumbnailPath) {

    }
}

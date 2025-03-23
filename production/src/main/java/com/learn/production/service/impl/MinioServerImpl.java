package com.learn.production.service.impl;

import com.learn.production.service.MinioServer;
import com.learn.production.service.UrlService;
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
    private final UrlService urlService;
    @Value("${minio.bucket}")
    private String bucket;

    /**
     * 上传文件
     *
     * @param filename    文件名
     * @param inputStream 文件
     * @param contentType 文件类型
     */
    @Override
    public void uploadFile(String filename, InputStream inputStream, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucket)
                .stream(inputStream, -1, 10 * 1024 * 1024)
                .object(filename)
                .contentType(contentType)
                .build();
        minioClient.putObject(args);
        log.info("文件上传成功");
    }
    /**
     * 把本地文件保存到minio
     * @param filename minio存放路径
     * @param localFireName 本地存放路径
     * @param contentType 文件类型
     */
    @Override
    public void uploadLocalFile(String filename, String localFireName, String contentType) throws IOException{
        UploadObjectArgs args = UploadObjectArgs.builder()
                .bucket(bucket)
                .object(filename)
                .filename(localFireName)
                .contentType(contentType)
                .build();
        try {
            minioClient.uploadObject(args);
        }catch (Exception e){
            throw new IOException("本地文件上传失败");
        }

        log.info("本地文件上传成功");
    }

    /**
     * 合并文件
     *
     * @param id 视频id
     * @param total 总片数
     */
    @Override
    public void compose(Long id, @NotNull Integer total) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<ComposeSource> list = Stream.iterate(0, i -> ++i)
                .limit(total)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket)
                        .object(urlService.getMinioUrlById(id,i))
                        .build())
                .collect(Collectors.toList());
        ComposeObjectArgs args = ComposeObjectArgs.builder()
                .bucket(bucket)
                .object(urlService.getMinioUrlById(id))
                .sources(list)
                .build();
        minioClient.composeObject(args);
        log.info("视频合并成功");
    }

    /**
     * 下载文件到本地
     *
     * @param filename      文件名
     * @param localFileName 本地路径
     */
    @Override
    @Async("downloadThreadPool")
    public void downloadVideoFileToLocal(String filename, String localFileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        DownloadObjectArgs args= DownloadObjectArgs.builder()
                .bucket(bucket)
                .object(filename)
                .filename(localFileName)
                .build();
        minioClient.downloadObject(args);
    }

    /**
     * 获取文件流
     *
     * @param url 文件存放路径
     * @return 文件
     */
    @Override
    public InputStream downloadVideoFile(String url) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucket)
                .object(url)
                .build();
        return minioClient.getObject(args);
    }


}

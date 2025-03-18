package com.learn.production.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                        .endpoint("http://192.168.101.128:9000")
                        .credentials("minioAdmin", "minioAdmin")
                        .build();
    }
}

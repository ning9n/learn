package com.learn.production.config;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    /*
    在 Spring Boot 中推荐使用 ThreadPoolTaskExecutor 而不是 Java 原生的 ThreadPoolExecutor

    ThreadPoolTaskExecutor 自动兼容 Spring 的 @EnableAsync 和 @Async 注解，无需额外配置即可实现异步任务调度。
    Spring Boot 的 ThreadPoolTaskExecutor 自动暴露线程池指标（如 executor.pool.size、executor.active.count），结合 Actuator 可实时监控：
    通过 Spring Cloud Alibaba Sentinel 或 Resilience4j，可动态扩缩容线程池（如根据 CPU 使用率自动调整 maxPoolSize）。
     */

    //下载视频到本地线程池
    @Bean("downloadThreadPool")
    public ThreadPoolTaskExecutor downloadThreadPool(){
        ThreadPoolTaskExecutor downloadThreadPool=new ThreadPoolTaskExecutor();
        downloadThreadPool.setCorePoolSize(16);//TODO 根据带宽调整
        downloadThreadPool.setMaxPoolSize(32);
        downloadThreadPool.setQueueCapacity(1000);            // 防止拒绝
        downloadThreadPool.setThreadNamePrefix("Download-");
        downloadThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        downloadThreadPool.initialize();
        return downloadThreadPool;
    }
    //上传本地视频到minio线程池
    @Bean("uploadThreadPool")
    public ThreadPoolTaskExecutor uploadThreadPool(){
        ThreadPoolTaskExecutor uploadThreadPool=new ThreadPoolTaskExecutor();
        uploadThreadPool.setCorePoolSize(16);//TODO 根据带宽调整
        uploadThreadPool.setMaxPoolSize(32);
        uploadThreadPool.setQueueCapacity(1000);            // 防止拒绝
        uploadThreadPool.setThreadNamePrefix("Upload-");
        uploadThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        uploadThreadPool.initialize();
        return uploadThreadPool;
    }
    //解析视频获取时长、封面线程池
    @Bean(name = "parseThreadPool")
    public ThreadPoolTaskExecutor parseThreadPool(){
        ThreadPoolTaskExecutor parseThreadPool=new ThreadPoolTaskExecutor();
        parseThreadPool.setCorePoolSize(8);
        parseThreadPool.setMaxPoolSize(16);
        parseThreadPool.setQueueCapacity(0);
        parseThreadPool.setThreadNamePrefix("Parse-");
        parseThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        parseThreadPool.initialize();
        return parseThreadPool;
    }
    //转码视频线程池
    @Bean(name = "transcodeThreadPool")
    public ThreadPoolTaskExecutor transcodeThread(){
        ThreadPoolTaskExecutor transcodeThreadPool=new ThreadPoolTaskExecutor();
        transcodeThreadPool.setCorePoolSize(8);
        transcodeThreadPool.setMaxPoolSize(16);
        transcodeThreadPool.setQueueCapacity(0);
        transcodeThreadPool.setThreadNamePrefix("Transcode-");
        transcodeThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        transcodeThreadPool.initialize();
        return transcodeThreadPool;
    }


}

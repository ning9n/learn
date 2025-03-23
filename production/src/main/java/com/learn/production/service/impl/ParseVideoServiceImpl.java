package com.learn.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.learn.api.domain.po.production.Production;
import com.learn.production.mapper.ProductionMapper;
import com.learn.production.service.FFmpegService;
import com.learn.production.service.MinioServer;
import com.learn.production.service.ParseVideoService;
import com.learn.production.service.UrlService;
import com.learn.production.utils.ProductionConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParseVideoServiceImpl implements ParseVideoService {
    private final MinioServer minioServer;
    private final ProductionMapper productionMapper;
    private final FFmpegService ffmpegService;
    private final RedissonClient redissonClient;
    private final UrlService urlService;
    /**
     * 解析视频，获取时长和封面
     * 只需要第一帧即可尝试解析
     * -
     * 三种情况：
     * 第一帧解析成功
     * 第一帧解析失败，合并后解析
     * 合并后第一帧未解析完，且第一帧解析失败，需要再次调用解析
     *
     * @param id    作品id
     * @param part  是不是分片文件
     */
    @Override
    @Async("parseThreadPool")
    public void parseVideo(Long id, boolean part) throws IOException {
        //使用redisson乐观锁避免重复解析
        RBucket<Boolean> bucket = redissonClient.getBucket(ProductionConstants.VIDEO_PARSE_KEY + id+":"+part);
        Boolean using = bucket.get();
        if (using!=null&&(using || !bucket.compareAndSet(false, true))) {
            return;
        }
        //由于会删除锁，只有服务宕机才会用到过期删除
        bucket.set(true,ProductionConstants.VIDEO_PARSE_TTL,ProductionConstants.VIDEO_PARSE_TIMEUNIT);
        try {
            //解析视频，获取视频时长，封面
            String localThumbnailPath = urlService.getLocalThumbnailUrlById(id);
            String duration;
            try {
                String localVideoPath;
                if(part){
                    localVideoPath=urlService.getLocalUrlById(id,0);
                }else{
                    localVideoPath=urlService.getLocalUrlById(id);
                }
                duration = ffmpegService.getVideoDuration(localVideoPath);
                log.info("视频时长："+duration);
                ffmpegService.extractVideoThumbnail(localVideoPath, localThumbnailPath);
            }
            //解析失败
            catch (Exception e){
                //如果是对完整视频解析，报错
                if(!part){
                    throw new IOException("视频解析失败");
                }
                //第一片解析失败
                //如果视频未合并完成，直接返回，后续会解析合并后的视频
                LambdaQueryWrapper<Production> wrapper=new LambdaQueryWrapper<Production>()
                        .eq(Production::getId,id)
                        .select(Production::getStatus);
                Production production = productionMapper.selectOne(wrapper);
                //合并完成
                if(production.getStatus()==2){
                    parseVideo(id,false);
                }
                log.info("解析失败");
                //未合并完成
                return;
            }
            //更新与保存

            String thumbnailPath = urlService.getMinioThumbnailUrlById(id);
            //保存封面到minio
            minioServer.uploadLocalFile(thumbnailPath,localThumbnailPath,"image/png" );
            //删除本地封面
            File file = new File(localThumbnailPath);
            if (!file.delete()) {
                throw new IOException("视频封面删除失败，视频id:" + id.toString());
            }
            //更新数据
            LambdaUpdateWrapper<Production> updateWrapper = new LambdaUpdateWrapper<Production>()
                    .eq(Production::getId, id)
                    .set(Production::getLength, duration)
                    .set(Production::getCover, thumbnailPath)
                    .set(Production::getStatus,3);
            productionMapper.update(null, updateWrapper);
        } finally {
            //释放锁
            bucket.delete();
        }

    }
}

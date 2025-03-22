package com.learn.production.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.learn.api.domain.dto.production.UploadPartVideoDto;
import com.learn.api.domain.po.production.IncompleteProduction;
import com.learn.api.domain.po.production.Production;
import com.learn.production.mapper.IncompleteProductionMapper;
import com.learn.production.mapper.ProductionMapper;
import com.learn.production.service.FFmpegService;
import com.learn.production.service.MinioServer;
import com.learn.production.service.VideoService;
import com.learn.production.utils.ProductionConstants;
import com.learn.production.utils.RedisIdWorker;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final RedisIdWorker idWorker;
    private final MinioServer minioServer;
    private final ProductionMapper productionMapper;
    private final IncompleteProductionMapper incompleteProductionMapper;
    private final FFmpegService ffmpegService;
    @Value("${ffmpeg.temporary-path}")
    private String temporaryPathPrefix;

    @Override
    public Long uploadTotalVideo(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InterruptedException {
        Long id=idWorker.nextId(ProductionConstants.VIDEO_ID_KEY_PREFIX);
        LocalDateTime now=idWorker.getCreateTime(id);
        //路径为年/月/日/id
        String url=now.getYear()+"/"+now.getMonth()+"/"+now.getDayOfMonth()+"/"+id;
        //上传到minio
        minioServer.uploadVideoFile(url,file.getInputStream(), file.getContentType());
        //保存到数据库
        Long userId = Long.parseLong((String) StpUtil.getLoginId());
        Production production=new Production();
        production.setId(id);
        production.setUrl(url);
        production.setUserId(userId);
        production.setStatus(2);//解析中
        productionMapper.insert(production);
        //TODO 异步处理
        /*analysisVideo(id,url);*/
        return id;
    }
    /*
                                   第一片 ->保存视频信息、上传信息到数据库                                    -|                              尝试解析视频
    创建或获取作品id-->上传到minio->--中间片 ->修改上传信息                                                   -->保存视频到本地(用于解析或转码)-->
                                   最后片 ->修改作品状态（合并中）->合并视频->-已经解析完-->修改视频状态（合并完）-|
                                                                          未解析完  -->再次解析视频
     */
    @Override
    public Long uploadPartVideo(UploadPartVideoDto dto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InterruptedException {

        //创建或获取作品id
        Long id = dto.getId();
        if(id==null){
            id=idWorker.nextId(ProductionConstants.VIDEO_ID_KEY_PREFIX);
        }

        //根据id获取minio存放路径
        LocalDateTime now=idWorker.getCreateTime(id);
        //路径为年/月/日/id/chunk/part
        String url=now.getYear()+"/"+now.getMonth()+"/"+now.getDayOfMonth()+"/"+id+"/chunk/"+dto.getPart();

        //上传到minio
        minioServer.uploadVideoFile(url,dto.getFile().getInputStream(),dto.getFile().getContentType());

        //获取用户id
        Long userId = Long.parseLong((String) StpUtil.getLoginId());
        //第一片，保存视频信息，上传信息
        if(dto.getPart()==0){
            saveVideoAndUploadInfo(dto, id, now, userId);
        }
        //中间片
        //修改视频上传信息
        else if(!dto.getPart().equals(dto.getTotal()-1)){
            LambdaUpdateWrapper<IncompleteProduction> wrapper=new LambdaUpdateWrapper<IncompleteProduction>()
                    .eq(IncompleteProduction::getId,id)
                    .set(IncompleteProduction::getPart,dto.getPart());
            incompleteProductionMapper.update(null,wrapper);
        }
        //最后一片
        else{
            //修改作品状态
            LambdaQueryWrapper<Production> queryWrapper=new LambdaQueryWrapper<Production>()
                    .eq(Production::getId,id)
                    .select(Production::getUrl);
            Production production = productionMapper.selectOne(queryWrapper);
            LambdaUpdateWrapper<Production> updateWrapper=new LambdaUpdateWrapper<Production>()
                    .eq(Production::getId,id)
                    .set(Production::getStatus,1);//合并中
            productionMapper.update(null,updateWrapper);
            //删除文件上传信息
            incompleteProductionMapper.deleteById(id);
            //合并视频，由于路径重复，合并会自动删除分片视频
            minioServer.compose(production.getUrl(),dto.getTotal());
            //更新作品状态
            LambdaUpdateWrapper<Production> wrapper=new LambdaUpdateWrapper<Production>()
                    .eq(Production::getId,id)
                            .set(Production::getStatus,2);//解析中
            productionMapper.update(null,wrapper);
        }

       /* //异步下载分片视频到本地，用于后续的解析、转码
        String temporaryPath=temporaryPathPrefix+id+"/chunk/"+dto.getPart();
        downloadToLocal(dto);
        //如果是第一片，尝试异步解析
        if(dto.getPart()==0){
            analysisVideo(id,url,temporaryPath);
        }*/
        return id;
    }

    /**
     * 把视频信息和上传信息保存到mysql
     * @param dto 视频信息
     * @param id 视频id
     * @param createTime 视频创建时间
     * @param userId 用户id
     */
    private void saveVideoAndUploadInfo(UploadPartVideoDto dto, Long id, LocalDateTime createTime, Long userId) {
        //保存视频信息到mysql
        Production production=new Production();
        production.setId(id);
        production.setUrl(createTime.getYear()+"/"+ createTime.getMonth()+"/"+ createTime.getDayOfMonth()+"/"+ id);
        production.setUserId(userId);
        production.setStatus(0);//上传中
        productionMapper.insert(production);
        //保存视频上传信息到mysql
        IncompleteProduction incompleteProduction=new IncompleteProduction();
        incompleteProduction.setId(id);
        incompleteProduction.setUserId(userId);
        incompleteProduction.setPart(0);//上传的分片数
        incompleteProduction.setTotal(dto.getTotal());
        incompleteProduction.setCreateTime(LocalDateTime.now());
        incompleteProductionMapper.insert(incompleteProduction);
    }

    /**
     * 把文件保存到本地
     * @param dto 文件信息
     * @throws IOException 保存失败
     */
    @Async("downloadThreadPool")
    protected void downloadToLocal(UploadPartVideoDto dto) throws IOException {
        String localPath=temporaryPathPrefix+dto.getId()+"/chunk/"+dto.getPart();
        File file=new File(localPath);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            if(!dir.mkdirs()){
                throw new IOException("本地文件创建失败");
            }
        }
        // 保存文件
        dto.getFile().transferTo(file);

    }

    @Override
    public void uploadCover(Long productionId, MultipartFile file) {

        /*minioServer.uploadVideoFile();*/
    }

    /**
     * 解析视频，获取时长和封面
     * 只需要第一帧即可解析
     */
    @Async("parseThreadPool")
    protected void analysisVideo(Long id,String url, String temporaryPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InterruptedException {

        minioServer.downloadVideoFileToLocal(url,temporaryPath); //解析视频，获取视频时长，封面
        String duration =ffmpegService.getVideoDuration(temporaryPath);
        String localThumbnailPath=temporaryPathPrefix+"/thumbnail/"+id;
        ffmpegService.extractVideoThumbnail(temporaryPath,localThumbnailPath);
        //删除本地视频
        File file= new File(temporaryPath);
        if(!file.delete()){
            throw new IOException("视频删除失败，视频id:"+id.toString());
        }
        //保存封面到minio
        String thumbnailPath=url.substring(0,url.lastIndexOf(String.valueOf(id)))+"/thumbnail"+id;
        minioServer.uploadLocalFile(localThumbnailPath,thumbnailPath);
        //删除本地封面
        file = new File(localThumbnailPath);
        if(!file.delete()){
            throw new IOException("视频封面删除失败，视频id:"+id.toString());
        }
        //更新数据
        LambdaUpdateWrapper<Production> updateWrapper=new LambdaUpdateWrapper<Production>()
                .eq(Production::getId,id)
                        .set(Production::getLength,duration)
                                .set(Production::getCover,thumbnailPath);
        productionMapper.update(null,updateWrapper);
    }

}

package com.learn.production.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.learn.api.domain.dto.production.UploadPartVideoDto;
import com.learn.api.domain.po.production.IncompleteProduction;
import com.learn.api.domain.po.production.Production;
import com.learn.production.mapper.IncompleteProductionMapper;
import com.learn.production.mapper.ProductionMapper;
import com.learn.production.service.*;
import com.learn.production.utils.ProductionConstants;
import com.learn.production.utils.RedisIdWorker;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UrlService urlService;
    private final ParseVideoService parseVideoService;


    @Override
    public Long uploadTotalVideo(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InterruptedException {
        Long id = idWorker.nextId(ProductionConstants.VIDEO_ID_KEY_PREFIX);
        LocalDateTime now = idWorker.getCreateTime(id);
        //路径为年/月/日/id
        String url = urlService.getMinioUrlById(id);
        //上传到minio
        minioServer.uploadFile(url, file.getInputStream(), file.getContentType());
        //保存到数据库
        Long userId = Long.parseLong((String) StpUtil.getLoginId());
        Production production = new Production();
        production.setId(id);
        production.setUrl(url);
        production.setUserId(userId);
        production.setStatus(2);//解析中
        production.setCreateTime(LocalDateTime.now());
        productionMapper.insert(production);
        //保存到本地
        downloadToLocal(file, urlService.getLocalUrlById(id));
        //解析视频
        parseVideoService.parseVideo(id,false);
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

        // 创建或获取作品id
        Long id = dto.getId();
        if (id == null) {
            id = idWorker.nextId(ProductionConstants.VIDEO_ID_KEY_PREFIX);
            log.info("创建新的作品id: {}", id);
        }

        //上传到minio
        String url = urlService.getMinioUrlById(id, dto.getPart());
        minioServer.uploadFile(url, dto.getFile().getInputStream(), dto.getFile().getContentType());

        //获取用户id
        Long userId = Long.parseLong((String) StpUtil.getLoginId());

        //处理不同分片
        handleChunk(dto, id, userId);

        //异步下载分片视频到本地，用于后续的解析、转码
        String temporaryPath = urlService.getLocalUrlById(id, dto.getPart());
        downloadToLocal(dto.getFile(), temporaryPath);

        //如果是第一片，尝试异步解析
        if (dto.getPart() == 0) {
            parseVideoService.parseVideo(id,true);
        }
        return id;
    }

    /**
     * 处理不同分片
     * @param dto 分片上传参数
     * @param id 视频id
     * @param userId 用户id
     */
    private void handleChunk(UploadPartVideoDto dto, Long id, Long userId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //第一片，保存视频信息，上传信息
        if (dto.getPart() == 0) {
            handleFirstChunk(dto, id, LocalDateTime.now(), userId);
        }
        //中间片
        //修改视频上传信息
        else if (!dto.getPart().equals(dto.getTotal() - 1)) {
            LambdaUpdateWrapper<IncompleteProduction> wrapper = new LambdaUpdateWrapper<IncompleteProduction>()
                    .eq(IncompleteProduction::getId, id)
                    .set(IncompleteProduction::getPart, dto.getPart());
            incompleteProductionMapper.update(null, wrapper);
        }
        //最后一片
        else {
            handleLastChunk(dto, id);
        }
    }
    /**
     * 处理第一片视频
     * 把视频信息和上传信息保存到mysql
     *
     * @param dto        视频信息
     * @param id         视频id
     * @param createTime 视频创建时间
     * @param userId     用户id
     */
    private void handleFirstChunk(UploadPartVideoDto dto, Long id, LocalDateTime createTime, Long userId) {
        //保存视频信息到mysql
        Production production = new Production();
        production.setId(id);
        production.setUrl(createTime.getYear() + "/" + createTime.getMonth() + "/" + createTime.getDayOfMonth() + "/" + id);
        production.setUserId(userId);
        production.setStatus(0);//上传中
        productionMapper.insert(production);
        //保存视频上传信息到mysql
        IncompleteProduction incompleteProduction = new IncompleteProduction();
        incompleteProduction.setId(id);
        incompleteProduction.setUserId(userId);
        incompleteProduction.setPart(0);//上传的分片数
        incompleteProduction.setTotal(dto.getTotal());
        incompleteProduction.setCreateTime(LocalDateTime.now());
        incompleteProductionMapper.insert(incompleteProduction);
    }


    /**
     * 处理分片的最后一片
     * @param dto 分片上传参数
     * @param id 视频id
     */
    private void handleLastChunk(UploadPartVideoDto dto, Long id) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //修改作品状态
        LambdaQueryWrapper<Production> queryWrapper = new LambdaQueryWrapper<Production>()
                .eq(Production::getId, id)
                .select(Production::getUrl);
        Production production = productionMapper.selectOne(queryWrapper);
        LambdaUpdateWrapper<Production> updateWrapper = new LambdaUpdateWrapper<Production>()
                .eq(Production::getId, id)
                .set(Production::getStatus, 1);//合并中
        productionMapper.update(null, updateWrapper);
        //删除文件上传信息
        incompleteProductionMapper.deleteById(id);
        //合并视频
        minioServer.compose(id, dto.getTotal());
        //TODO 删除视频
        //查询视频是否解析完
        //没有解析完更新状态为解析中，尝试异步解析
        //解析完更新状态为待发布
        LambdaQueryWrapper<Production> lambdaQueryWrapper=new LambdaQueryWrapper<Production>()
                .eq(Production::getId, id)
                ;
        Production one = productionMapper.selectOne(lambdaQueryWrapper);
        int status=3;
        if(one.getLength()==null){
            status=2;
            minioServer.downloadVideoFileToLocal(urlService.getMinioUrlById(id), urlService.getLocalUrlById(id));
            parseVideoService.parseVideo(id,false);
        }
        //更新作品状态
        LambdaUpdateWrapper<Production> wrapper = new LambdaUpdateWrapper<Production>()
                .eq(Production::getId, id)
                .set(Production::getStatus, status);//解析中
        productionMapper.update(null, wrapper);
    }



    /**
     * 把文件保存到本地
     *
     * @param file 文件
     * @throws IOException 保存失败
     */
    @Async("downloadThreadPool")
    protected void downloadToLocal(MultipartFile file, String localPath) throws IOException {
        File localFile = new File(localPath);
        File dir = localFile.getParentFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("本地文件创建失败");
            }
        }
        // 保存文件
        file.transferTo(localFile);

    }

    @Override
    public void uploadCover(Long productionId, MultipartFile file) {

        /*minioServer.uploadVideoFile();*/
    }




}

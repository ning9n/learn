package com.learn.production.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.learn.api.domain.dto.production.UploadPartVideoDto;
import com.learn.api.domain.po.production.IncompleteProduction;
import com.learn.api.domain.po.production.Production;
import com.learn.common.exception.PreviousChunkNotCompletedException;
import com.learn.production.mapper.IncompleteProductionMapper;
import com.learn.production.mapper.ProductionMapper;
import com.learn.production.service.MinioServer;
import com.learn.production.service.VideoService;
import com.learn.production.utils.ProductionConstants;
import com.learn.production.utils.RedisIdWorker;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    @Override
    public Long uploadTotalVideo(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
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
        analysisVideo(id);
        productionMapper.insert(production);
        return id;
    }

    @Override
    public Long uploadPartVideo(UploadPartVideoDto dto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Long id = dto.getId();
        if(id==null){
            id=idWorker.nextId(ProductionConstants.VIDEO_ID_KEY_PREFIX);
        }
        LocalDateTime now=idWorker.getCreateTime(id);
        //路径为年/月/日/id/chunk/part
        String url=now.getYear()+"/"+now.getMonth()+"/"+now.getDayOfMonth()+"/"+id+"/chunk/"+dto.getPart();
        Long userId = Long.parseLong((String) StpUtil.getLoginId());
        //上传到minio
        minioServer.uploadVideoFile(url,dto.getFile().getInputStream(),dto.getFile().getContentType());

        //第一片
        if(dto.getPart()==0){
            //保存视频信息
            Production production=new Production();
            production.setId(id);
            production.setUrl(now.getYear()+"/"+now.getMonth()+"/"+now.getDayOfMonth()+"/"+id);
            production.setUserId(userId);
            production.setStatus(0);
            productionMapper.insert(production);
            //保存视频上传信息
            IncompleteProduction incompleteProduction=new IncompleteProduction();
            incompleteProduction.setId(id);
            incompleteProduction.setUserId(userId);
            incompleteProduction.setPart(0);//上传中
            incompleteProduction.setTotal(dto.getTotal());
            incompleteProduction.setCreateTime(LocalDateTime.now());
            incompleteProductionMapper.insert(incompleteProduction);
        }
        //中间片
        //修改视频上传信息
        else if(!dto.getPart().equals(dto.getTotal()+1)){
            IncompleteProduction incompleteProduction = incompleteProductionMapper.selectById(id);
            if(incompleteProduction.getPart()!=dto.getPart()-1){
                throw new PreviousChunkNotCompletedException("上传异常：前一片还没有上传完");
            }
            incompleteProduction.setPart(dto.getPart());
        }
        //最后一片
        else{
            //修改作品状态
            Production production = productionMapper.selectById(id);
            production.setStatus(1);//合并中
            productionMapper.updateById(production);
            //删除文件上传信息
            incompleteProductionMapper.deleteById(id);
            //合并视频
            minioServer.compose(production.getUrl(),dto.getTotal());
            //更新作品状态
            //作品开始上传到解析完成期间不能修改信息，否则可能出现并发问题
            production.setStatus(2);//解析中
            productionMapper.updateById(production);
            analysisVideo(id);
        }

        return id;
    }

    private void analysisVideo(Long id) {
        //TODO 解析视频，获取视频时长，封面
    }

}

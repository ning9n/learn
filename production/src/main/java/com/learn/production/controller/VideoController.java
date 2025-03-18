package com.learn.production.controller;

import com.learn.api.domain.dto.production.UploadPartVideoDto;
import com.learn.common.exception.ArgumentException;
import com.learn.common.response.Response;
import com.learn.production.service.VideoService;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    /**
     * 上传完整视频
     * @return 视频id
     */
    @PostMapping("/uploadTotalVideo")
    public Response<Long> uploadTotalVideo(@RequestPart("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Long id=videoService.uploadTotalVideo(file);
        return Response.ok(id);
    }

    /**
     * 上传分片视频
     * @return 视频id
     */
    @PostMapping("/uploadPartVideo")
    public Response<Long> uploadPartVideo(UploadPartVideoDto dto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(dto.getId()==null&&dto.getPart()!=0){
            throw new ArgumentException("非第一片必须携带id");
        }
        Long id=videoService.uploadPartVideo(dto);
        return Response.ok(id);
    }
}

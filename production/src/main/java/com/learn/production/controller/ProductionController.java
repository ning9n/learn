package com.learn.production.controller;

import com.learn.api.domain.dto.production.PublishProductionDto;
import com.learn.common.response.Response;
import com.learn.production.service.ProductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductionController {
    private final ProductionService productionService;

    /**
     * 发布作品
     * @param dto 相关参数
     * @return 是否成功
     */
    @PostMapping
    public Response<Boolean> publishProduction(PublishProductionDto dto){
        Boolean success=productionService.publishProduction(dto);
        return Response.ok(success);
    }
}

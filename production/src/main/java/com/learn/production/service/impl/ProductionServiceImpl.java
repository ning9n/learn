package com.learn.production.service.impl;

import com.learn.api.domain.dto.production.PublishProductionDto;
import com.learn.api.domain.po.production.Production;
import com.learn.production.mapper.ProductionMapper;
import com.learn.production.service.ProductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements ProductionService {
    private final ProductionMapper productionMapper;
    @Override
    public Boolean publishProduction(PublishProductionDto dto) {
        Production production = productionMapper.selectById(dto.getId());
        production.setName(dto.getName());
        production.setIntro(dto.getIntro());
        production.setCreateTime(LocalDateTime.now());
        productionMapper.updateById(production);
        return true;
    }
}

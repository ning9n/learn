package com.learn.production.service;

import com.learn.api.domain.dto.production.PublishProductionDto;

public interface ProductionService {
    Boolean publishProduction(PublishProductionDto dto);
}

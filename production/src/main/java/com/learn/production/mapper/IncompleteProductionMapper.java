package com.learn.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learn.api.domain.po.production.IncompleteProduction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IncompleteProductionMapper extends BaseMapper<IncompleteProduction> {
}

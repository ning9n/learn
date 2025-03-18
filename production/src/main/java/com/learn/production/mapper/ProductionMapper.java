package com.learn.production.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learn.api.domain.po.production.Production;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductionMapper extends BaseMapper<Production> {
}

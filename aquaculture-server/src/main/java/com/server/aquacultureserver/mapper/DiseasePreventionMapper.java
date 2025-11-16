package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.DiseasePrevention;
import org.apache.ibatis.annotations.Mapper;

/**
 * 病害防治记录Mapper接口
 */
@Mapper
public interface DiseasePreventionMapper extends BaseMapper<DiseasePrevention> {
}


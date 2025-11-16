package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.DailyInspection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日常检查记录Mapper接口
 */
@Mapper
public interface DailyInspectionMapper extends BaseMapper<DailyInspection> {
}


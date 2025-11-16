package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.YieldStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产量统计Mapper接口
 */
@Mapper
public interface YieldStatisticsMapper extends BaseMapper<YieldStatistics> {
}


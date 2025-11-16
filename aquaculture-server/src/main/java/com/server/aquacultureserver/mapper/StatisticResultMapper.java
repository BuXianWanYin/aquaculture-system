package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.StatisticResult;
import org.apache.ibatis.annotations.Mapper;

/**
 * 统计结果Mapper接口
 */
@Mapper
public interface StatisticResultMapper extends BaseMapper<StatisticResult> {
}


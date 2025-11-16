package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.AquaculturePlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 养殖计划Mapper接口
 */
@Mapper
public interface AquaculturePlanMapper extends BaseMapper<AquaculturePlan> {
}


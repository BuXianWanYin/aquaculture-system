package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.MedicineUsage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用药记录Mapper接口
 */
@Mapper
public interface MedicineUsageMapper extends BaseMapper<MedicineUsage> {
}


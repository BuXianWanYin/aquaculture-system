package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.BaseEquipment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备信息Mapper接口
 */
@Mapper
public interface BaseEquipmentMapper extends BaseMapper<BaseEquipment> {
}


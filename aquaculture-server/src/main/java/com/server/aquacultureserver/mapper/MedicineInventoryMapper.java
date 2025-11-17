package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.MedicineInventory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 药品库存Mapper接口
 */
@Mapper
public interface MedicineInventoryMapper extends BaseMapper<MedicineInventory> {
}


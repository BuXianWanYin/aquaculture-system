package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.FeedInventory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 饲料库存Mapper接口
 */
@Mapper
public interface FeedInventoryMapper extends BaseMapper<FeedInventory> {
}


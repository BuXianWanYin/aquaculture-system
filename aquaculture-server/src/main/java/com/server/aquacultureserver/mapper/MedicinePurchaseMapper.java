package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.MedicinePurchase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 药品采购Mapper接口
 */
@Mapper
public interface MedicinePurchaseMapper extends BaseMapper<MedicinePurchase> {
}


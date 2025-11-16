package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.FeedPurchase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 饲料采购Mapper接口
 */
@Mapper
public interface FeedPurchaseMapper extends BaseMapper<FeedPurchase> {
}


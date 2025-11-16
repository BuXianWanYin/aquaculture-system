package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.FeedUsage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 饲料使用记录Mapper接口
 */
@Mapper
public interface FeedUsageMapper extends BaseMapper<FeedUsage> {
}


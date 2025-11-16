package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.FeedingRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 投喂记录Mapper接口
 */
@Mapper
public interface FeedingRecordMapper extends BaseMapper<FeedingRecord> {
}


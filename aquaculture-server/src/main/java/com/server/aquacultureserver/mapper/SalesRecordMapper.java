package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.SalesRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售记录Mapper接口
 */
@Mapper
public interface SalesRecordMapper extends BaseMapper<SalesRecord> {
}


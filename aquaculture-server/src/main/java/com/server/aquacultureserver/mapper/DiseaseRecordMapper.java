package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.DiseaseRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 病害记录Mapper接口
 */
@Mapper
public interface DiseaseRecordMapper extends BaseMapper<DiseaseRecord> {
}


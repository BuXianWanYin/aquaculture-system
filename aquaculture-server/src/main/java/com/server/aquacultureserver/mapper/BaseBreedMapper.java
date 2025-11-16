package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.BaseBreed;
import org.apache.ibatis.annotations.Mapper;

/**
 * 养殖品种Mapper接口
 */
@Mapper
public interface BaseBreedMapper extends BaseMapper<BaseBreed> {
}


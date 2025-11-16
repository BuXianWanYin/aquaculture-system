package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}


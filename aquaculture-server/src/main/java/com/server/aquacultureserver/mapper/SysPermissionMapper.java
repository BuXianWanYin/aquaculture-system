package com.server.aquacultureserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.aquacultureserver.domain.SysPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限Mapper接口
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
}


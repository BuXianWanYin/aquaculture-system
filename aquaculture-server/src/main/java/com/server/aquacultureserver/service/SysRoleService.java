package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService {
    
    /**
     * 查询所有角色
     */
    List<SysRole> getAllRoles();
    
    /**
     * 分页查询角色列表
     */
    Page<SysRole> getPage(Integer current, Integer size, String roleName);
    
    /**
     * 根据ID查询角色
     */
    SysRole getById(Long roleId);
    
    /**
     * 新增角色
     */
    boolean saveRole(SysRole role);
    
    /**
     * 更新角色
     */
    boolean updateRole(SysRole role);
    
    /**
     * 删除角色
     */
    boolean deleteRole(Long roleId);
}


package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysPermission;

import java.util.List;

/**
 * 权限服务接口
 */
public interface SysPermissionService {
    
    /**
     * 查询所有权限
     */
    List<SysPermission> getAllPermissions();
    
    /**
     * 分页查询权限列表
     */
    Page<SysPermission> getPage(Integer current, Integer size, String module, String keyword);
    
    /**
     * 根据ID查询权限
     */
    SysPermission getById(Long permissionId);
    
    /**
     * 根据模块查询权限列表
     */
    List<SysPermission> getByModule(String module);
    
    /**
     * 根据父权限ID查询子权限列表
     */
    List<SysPermission> getByParentId(Long parentId);
    
    /**
     * 新增权限
     */
    boolean savePermission(SysPermission permission);
    
    /**
     * 更新权限
     */
    boolean updatePermission(SysPermission permission);
    
    /**
     * 删除权限
     */
    boolean deletePermission(Long permissionId);
    
    /**
     * 根据角色ID查询权限列表
     */
    List<SysPermission> getPermissionsByRoleId(Long roleId);
    
    /**
     * 为角色分配权限
     */
    boolean assignPermissionsToRole(Long roleId, Long[] permissionIds);
    
    /**
     * 移除角色的所有权限
     */
    boolean removeAllPermissionsFromRole(Long roleId);
}


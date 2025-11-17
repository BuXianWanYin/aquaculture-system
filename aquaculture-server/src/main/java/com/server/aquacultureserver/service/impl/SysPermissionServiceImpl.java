package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysPermission;
import com.server.aquacultureserver.domain.SysRolePermission;
import com.server.aquacultureserver.mapper.SysPermissionMapper;
import com.server.aquacultureserver.mapper.SysRolePermissionMapper;
import com.server.aquacultureserver.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
public class SysPermissionServiceImpl implements SysPermissionService {
    
    @Autowired
    private SysPermissionMapper permissionMapper;
    
    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;
    
    /**
     * 查询所有权限
     * @return 权限列表（按排序顺序和权限ID升序）
     */
    @Override
    public List<SysPermission> getAllPermissions() {
        return permissionMapper.selectList(
            new LambdaQueryWrapper<SysPermission>()
                .orderByAsc(SysPermission::getSortOrder)
                .orderByAsc(SysPermission::getPermissionId)
        );
    }
    
    /**
     * 分页查询权限列表
     * @param current 当前页码
     * @param size 每页大小
     * @param module 模块名称（精确查询）
     * @param keyword 关键词（模糊查询权限名称或权限标识）
     * @return 分页结果
     */
    @Override
    public Page<SysPermission> getPage(Integer current, Integer size, String module, String keyword) {
        Page<SysPermission> page = new Page<>(current, size);
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        
        if (module != null && !module.isEmpty()) {
            wrapper.eq(SysPermission::getModule, module);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysPermission::getPermissionName, keyword)
                    .or()
                    .like(SysPermission::getPermissionCode, keyword));
        }
        
        wrapper.orderByAsc(SysPermission::getSortOrder)
               .orderByAsc(SysPermission::getPermissionId);
        return permissionMapper.selectPage(page, wrapper);
    }
    
    /**
     * 根据ID查询权限
     * @param permissionId 权限ID
     * @return 权限信息
     */
    @Override
    public SysPermission getById(Long permissionId) {
        return permissionMapper.selectById(permissionId);
    }
    
    /**
     * 根据模块名称查询权限列表
     * @param module 模块名称
     * @return 权限列表（按排序顺序）
     */
    @Override
    public List<SysPermission> getByModule(String module) {
        return permissionMapper.selectList(
            new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getModule, module)
                .orderByAsc(SysPermission::getSortOrder)
        );
    }
    
    /**
     * 根据父权限ID查询子权限列表
     * @param parentId 父权限ID
     * @return 子权限列表（按排序顺序）
     */
    @Override
    public List<SysPermission> getByParentId(Long parentId) {
        return permissionMapper.selectList(
            new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getParentId, parentId)
                .orderByAsc(SysPermission::getSortOrder)
        );
    }
    
    /**
     * 新增权限
     * 会检查权限标识是否已存在
     * @param permission 权限信息
     * @return 是否成功
     */
    @Override
    public boolean savePermission(SysPermission permission) {
        // 检查权限标识是否已存在
        SysPermission existPermission = permissionMapper.selectOne(
            new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getPermissionCode, permission.getPermissionCode())
        );
        if (existPermission != null) {
            throw new RuntimeException("权限标识已存在");
        }
        
        // 如果没有设置排序，设置为0
        if (permission.getSortOrder() == null) {
            permission.setSortOrder(0);
        }
        
        return permissionMapper.insert(permission) > 0;
    }
    
    /**
     * 更新权限信息
     * 如果更新了权限标识，会检查是否与其他权限重复
     * @param permission 权限信息
     * @return 是否成功
     */
    @Override
    public boolean updatePermission(SysPermission permission) {
        // 如果更新了权限标识，检查是否重复
        if (permission.getPermissionCode() != null) {
            SysPermission existPermission = permissionMapper.selectOne(
                new LambdaQueryWrapper<SysPermission>()
                    .eq(SysPermission::getPermissionCode, permission.getPermissionCode())
            );
            if (existPermission != null && !existPermission.getPermissionId().equals(permission.getPermissionId())) {
                throw new RuntimeException("权限标识已存在");
            }
        }
        
        return permissionMapper.updateById(permission) > 0;
    }
    
    /**
     * 删除权限（物理删除）
     * 删除前会检查是否有子权限，并同时删除角色权限关联
     * @param permissionId 权限ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean deletePermission(Long permissionId) {
        // 检查是否有子权限
        List<SysPermission> children = getByParentId(permissionId);
        if (!children.isEmpty()) {
            throw new RuntimeException("该权限下存在子权限，无法删除");
        }
        
        // 删除角色权限关联
        rolePermissionMapper.delete(
            new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getPermissionId, permissionId)
        );
        
        return permissionMapper.deleteById(permissionId) > 0;
    }
    
    /**
     * 根据角色ID查询权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Override
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        // 查询角色关联的权限ID列表
        List<SysRolePermission> rolePermissions = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId)
        );
        
        if (rolePermissions.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取权限ID列表
        List<Long> permissionIds = rolePermissions.stream()
            .map(SysRolePermission::getPermissionId)
            .collect(Collectors.toList());
        
        // 查询权限详情
        return permissionMapper.selectBatchIds(permissionIds);
    }
    
    /**
     * 为角色分配权限
     * 会自动添加选中权限的所有父权限，确保权限树结构的完整性
     * @param roleId 角色ID
     * @param permissionIds 权限ID数组
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean assignPermissionsToRole(Long roleId, Long[] permissionIds) {
        // 先删除该角色的所有权限
        rolePermissionMapper.delete(
            new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId)
        );
        
        // 批量插入新权限
        if (permissionIds != null && permissionIds.length > 0) {
            // 使用Set去重，避免重复添加
            Set<Long> allPermissionIds = new HashSet<>();
            
            // 先添加所有直接分配的权限
            for (Long permissionId : permissionIds) {
                allPermissionIds.add(permissionId);
                
                // 自动添加父权限
                SysPermission permission = permissionMapper.selectById(permissionId);
                if (permission != null && permission.getParentId() != null && permission.getParentId() != 0) {
                    // 递归添加所有父权限
                    addParentPermissions(permission.getParentId(), allPermissionIds);
                }
            }
            
            // 批量插入所有权限（包括子权限和父权限）
            for (Long permissionId : allPermissionIds) {
                SysRolePermission rolePermission = new SysRolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
        
        return true;
    }
    
    /**
     * 递归添加父权限
     * 用于确保权限树结构的完整性，当分配子权限时自动添加其所有父权限
     * @param parentId 父权限ID
     * @param allPermissionIds 所有权限ID集合
     */
    private void addParentPermissions(Long parentId, Set<Long> allPermissionIds) {
        if (parentId == null || parentId == 0) {
            return;
        }
        
        // 如果已经添加过，避免重复
        if (allPermissionIds.contains(parentId)) {
            return;
        }
        
        // 添加父权限
        allPermissionIds.add(parentId);
        
        // 继续查找父权限的父权限
        SysPermission parentPermission = permissionMapper.selectById(parentId);
        if (parentPermission != null && parentPermission.getParentId() != null && parentPermission.getParentId() != 0) {
            addParentPermissions(parentPermission.getParentId(), allPermissionIds);
        }
    }
    
    /**
     * 移除角色的所有权限
     * @param roleId 角色ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean removeAllPermissionsFromRole(Long roleId) {
        return rolePermissionMapper.delete(
            new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId)
        ) >= 0;
    }
}


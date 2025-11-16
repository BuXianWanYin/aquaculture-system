package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysRole;
import com.server.aquacultureserver.mapper.SysRoleMapper;
import com.server.aquacultureserver.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {
    
    @Autowired
    private SysRoleMapper roleMapper;
    
    @Override
    public List<SysRole> getAllRoles() {
        return roleMapper.selectList(
            new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getRoleId)
        );
    }
    
    @Override
    public Page<SysRole> getPage(Integer current, Integer size, String roleName) {
        Page<SysRole> page = new Page<>(current, size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        
        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        
        wrapper.orderByDesc(SysRole::getCreateTime);
        return roleMapper.selectPage(page, wrapper);
    }
    
    @Override
    public SysRole getById(Long roleId) {
        return roleMapper.selectById(roleId);
    }
    
    @Override
    public boolean saveRole(SysRole role) {
        // 检查角色名是否已存在
        SysRole existRole = roleMapper.selectOne(
            new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, role.getRoleName())
        );
        if (existRole != null) {
            throw new RuntimeException("角色名称已存在");
        }
        
        return roleMapper.insert(role) > 0;
    }
    
    @Override
    public boolean updateRole(SysRole role) {
        // 如果更新了角色名，检查是否重复
        if (role.getRoleName() != null) {
            SysRole existRole = roleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>()
                    .eq(SysRole::getRoleName, role.getRoleName())
            );
            if (existRole != null && !existRole.getRoleId().equals(role.getRoleId())) {
                throw new RuntimeException("角色名称已存在");
            }
        }
        
        return roleMapper.updateById(role) > 0;
    }
    
    @Override
    public boolean deleteRole(Long roleId) {
        return roleMapper.deleteById(roleId) > 0;
    }
}


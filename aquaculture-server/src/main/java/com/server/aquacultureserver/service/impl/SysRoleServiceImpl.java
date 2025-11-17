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
    
    /**
     * 查询所有有效的角色
     * @return 角色列表（按角色ID升序）
     */
    @Override
    public List<SysRole> getAllRoles() {
        return roleMapper.selectList(
            new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getRoleId)
        );
    }
    
    /**
     * 分页查询角色列表
     * @param current 当前页码
     * @param size 每页大小
     * @param roleName 角色名称（模糊查询）
     * @return 分页结果
     */
    @Override
    public Page<SysRole> getPage(Integer current, Integer size, String roleName) {
        Page<SysRole> page = new Page<>(current, size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        
        // 角色名称模糊查询
        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(SysRole::getCreateTime);
        return roleMapper.selectPage(page, wrapper);
    }
    
    /**
     * 根据ID查询角色
     * @param roleId 角色ID
     * @return 角色信息
     */
    @Override
    public SysRole getById(Long roleId) {
        return roleMapper.selectById(roleId);
    }
    
    /**
     * 新增角色
     * 会检查角色名称是否已存在
     * @param role 角色信息
     * @return 是否成功
     */
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
    
    /**
     * 更新角色信息
     * 如果更新了角色名，会检查是否与其他角色重复
     * @param role 角色信息
     * @return 是否成功
     */
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
    
    /**
     * 删除角色（物理删除）
     * @param roleId 角色ID
     * @return 是否成功
     */
    @Override
    public boolean deleteRole(Long roleId) {
        return roleMapper.deleteById(roleId) > 0;
    }
}


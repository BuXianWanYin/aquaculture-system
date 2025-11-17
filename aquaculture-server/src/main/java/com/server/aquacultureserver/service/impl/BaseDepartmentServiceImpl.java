package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseDepartment;
import com.server.aquacultureserver.domain.BaseArea;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.mapper.BaseDepartmentMapper;
import com.server.aquacultureserver.mapper.BaseAreaMapper;
import com.server.aquacultureserver.service.BaseDepartmentService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 部门服务实现类
 */
@Service
public class BaseDepartmentServiceImpl implements BaseDepartmentService {
    
    @Autowired
    private BaseDepartmentMapper departmentMapper;
    
    @Autowired
    private BaseAreaMapper areaMapper;
    
    @Override
    public List<BaseDepartment> getAllDepartments() {
        LambdaQueryWrapper<BaseDepartment> wrapper = new LambdaQueryWrapper<>();
        
        // 未登录用户（注册场景）可以查看所有部门
        SysUser currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            wrapper.eq(BaseDepartment::getStatus, 1)
                   .orderByDesc(BaseDepartment::getCreateTime);
            return departmentMapper.selectList(wrapper);
        }
        
        // 系统管理员可以查看所有部门
        if (UserContext.isAdmin()) {
            wrapper.eq(BaseDepartment::getStatus, 1)
                   .orderByDesc(BaseDepartment::getCreateTime);
            return departmentMapper.selectList(wrapper);
        }
        
        // 部门管理员只能查看自己所在的部门
        if (UserContext.isDepartmentManager()) {
            if (currentUser.getDepartmentId() != null) {
                wrapper.eq(BaseDepartment::getDepartmentId, currentUser.getDepartmentId())
                       .eq(BaseDepartment::getStatus, 1)
                       .orderByDesc(BaseDepartment::getCreateTime);
                return departmentMapper.selectList(wrapper);
            } else {
                return Collections.emptyList();
            }
        }
        
        // 普通操作员只能查看自己所属的部门
        if (UserContext.isOperator()) {
            Long departmentId = null;
            
            // 优先使用用户表的departmentId
            if (currentUser.getDepartmentId() != null) {
                departmentId = currentUser.getDepartmentId();
            }
            // 如果没有departmentId，通过areaId查找区域的departmentId
            else if (currentUser.getAreaId() != null && areaMapper != null) {
                BaseArea area = areaMapper.selectById(currentUser.getAreaId());
                if (area != null && area.getDepartmentId() != null) {
                    departmentId = area.getDepartmentId();
                }
            }
            
            if (departmentId != null) {
                wrapper.eq(BaseDepartment::getDepartmentId, departmentId)
                       .eq(BaseDepartment::getStatus, 1)
                       .orderByDesc(BaseDepartment::getCreateTime);
                return departmentMapper.selectList(wrapper);
            } else {
                return Collections.emptyList();
            }
        }
        
        // 其他角色返回所有部门（兼容性处理）
        wrapper.eq(BaseDepartment::getStatus, 1)
               .orderByDesc(BaseDepartment::getCreateTime);
        return departmentMapper.selectList(wrapper);
    }
    
    @Override
    public BaseDepartment getById(Long departmentId) {
        return departmentMapper.selectById(departmentId);
    }
    
    @Override
    public Page<BaseDepartment> getPage(Integer current, Integer size, String departmentName, Integer status) {
        Page<BaseDepartment> page = new Page<>(current, size);
        LambdaQueryWrapper<BaseDepartment> wrapper = new LambdaQueryWrapper<>();
        
        if (departmentName != null && !departmentName.isEmpty()) {
            wrapper.like(BaseDepartment::getDepartmentName, departmentName);
        }
        if (status != null) {
            wrapper.eq(BaseDepartment::getStatus, status);
        }
        
        wrapper.orderByDesc(BaseDepartment::getCreateTime);
        return departmentMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveDepartment(BaseDepartment department) {
        // 检查部门编号是否已存在
        LambdaQueryWrapper<BaseDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseDepartment::getDepartmentCode, department.getDepartmentCode());
        BaseDepartment exist = departmentMapper.selectOne(wrapper);
        if (exist != null) {
            throw new RuntimeException("部门编号已存在");
        }
        
        if (department.getStatus() == null) {
            department.setStatus(1); // 默认启用
        }
        return departmentMapper.insert(department) > 0;
    }
    
    @Override
    public boolean updateDepartment(BaseDepartment department) {
        // 检查部门编号是否与其他部门重复
        LambdaQueryWrapper<BaseDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseDepartment::getDepartmentCode, department.getDepartmentCode())
               .ne(BaseDepartment::getDepartmentId, department.getDepartmentId());
        BaseDepartment exist = departmentMapper.selectOne(wrapper);
        if (exist != null) {
            throw new RuntimeException("部门编号已存在");
        }
        return departmentMapper.updateById(department) > 0;
    }
    
    @Override
    public boolean deleteDepartment(Long departmentId) {
        // 检查是否有区域关联此部门
        // TODO: 可以添加检查逻辑，如果有区域关联则不允许删除
        return departmentMapper.deleteById(departmentId) > 0;
    }
}


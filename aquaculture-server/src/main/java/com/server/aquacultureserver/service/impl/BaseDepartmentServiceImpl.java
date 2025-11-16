package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseDepartment;
import com.server.aquacultureserver.mapper.BaseDepartmentMapper;
import com.server.aquacultureserver.service.BaseDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门服务实现类
 */
@Service
public class BaseDepartmentServiceImpl implements BaseDepartmentService {
    
    @Autowired
    private BaseDepartmentMapper departmentMapper;
    
    @Override
    public List<BaseDepartment> getAllDepartments() {
        LambdaQueryWrapper<BaseDepartment> wrapper = new LambdaQueryWrapper<>();
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


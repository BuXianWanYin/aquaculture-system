package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseDepartment;

import java.util.List;

/**
 * 部门服务接口
 */
public interface BaseDepartmentService {
    
    /**
     * 查询所有部门（如果已登录则根据角色过滤，未登录则返回所有部门）
     */
    List<BaseDepartment> getAllDepartments();
    
    /**
     * 根据ID查询部门
     */
    BaseDepartment getById(Long departmentId);
    
    /**
     * 分页查询部门列表
     */
    Page<BaseDepartment> getPage(Integer current, Integer size, String departmentName, Integer status);
    
    /**
     * 新增部门
     */
    boolean saveDepartment(BaseDepartment department);
    
    /**
     * 更新部门
     */
    boolean updateDepartment(BaseDepartment department);
    
    /**
     * 删除部门
     */
    boolean deleteDepartment(Long departmentId);
}


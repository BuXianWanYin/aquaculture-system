package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseArea;
import com.server.aquacultureserver.mapper.BaseAreaMapper;
import com.server.aquacultureserver.service.BaseAreaService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 养殖区域服务实现类
 */
@Service
public class BaseAreaServiceImpl implements BaseAreaService {
    
    @Autowired
    private BaseAreaMapper areaMapper;
    
    /**
     * 查询所有有效的养殖区域
     * 根据用户角色进行权限过滤：
     * - 普通操作员：只能查看自己绑定的区域
     * - 部门管理员：只能查看其部门下的所有区域
     * - 系统管理员：可以查看所有区域
     * @return 区域信息列表
     */
    @Override
    public List<BaseArea> getAllAreas() {
        LambdaQueryWrapper<BaseArea> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己绑定的区域
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(BaseArea::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下的所有区域
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(BaseArea::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        // 只查询状态为正常（1）的记录
        wrapper.eq(BaseArea::getStatus, 1)
               .orderByDesc(BaseArea::getCreateTime);
        return areaMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询区域信息
     * @param areaId 区域ID
     * @return 区域信息
     */
    @Override
    public BaseArea getById(Long areaId) {
        return areaMapper.selectById(areaId);
    }
    
    /**
     * 分页查询区域信息
     * 根据用户角色进行权限过滤
     * @param current 当前页码
     * @param size 每页大小
     * @param areaName 区域名称（模糊查询）
     * @param departmentId 部门ID（精确查询）
     * @param status 状态（1-启用，0-禁用）
     * @return 分页结果
     */
    @Override
    public Page<BaseArea> getPage(Integer current, Integer size, String areaName, Long departmentId, Integer status) {
        Page<BaseArea> page = new Page<>(current, size);
        LambdaQueryWrapper<BaseArea> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己绑定的区域
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(BaseArea::getAreaId, userAreaId);
            } else {
                return page; // 返回空结果
            }
        }
        // 部门管理员只能查看其部门下的所有区域
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(BaseArea::getAreaId, areaIds);
            } else {
                return page; // 返回空结果
            }
        }
        
        // 区域名称模糊查询
        if (areaName != null && !areaName.isEmpty()) {
            wrapper.like(BaseArea::getAreaName, areaName);
        }
        // 部门ID精确查询
        if (departmentId != null) {
            wrapper.eq(BaseArea::getDepartmentId, departmentId);
        }
        // 状态查询
        if (status != null) {
            wrapper.eq(BaseArea::getStatus, status);
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(BaseArea::getCreateTime);
        return areaMapper.selectPage(page, wrapper);
    }
    
    /**
     * 新增区域信息
     * 普通操作员无权新增区域
     * @param area 区域信息
     * @return 是否成功
     */
    @Override
    public boolean saveArea(BaseArea area) {
        // 普通操作员不能新增区域
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权新增区域");
        }
        return areaMapper.insert(area) > 0;
    }
    
    /**
     * 更新区域信息
     * 普通操作员无权修改区域
     * @param area 区域信息
     * @return 是否成功
     */
    @Override
    public boolean updateArea(BaseArea area) {
        // 普通操作员不能修改区域
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权修改区域");
        }
        return areaMapper.updateById(area) > 0;
    }
    
    /**
     * 删除区域（物理删除）
     * 普通操作员无权删除区域
     * @param areaId 区域ID
     * @return 是否成功
     */
    @Override
    public boolean deleteArea(Long areaId) {
        // 普通操作员不能删除区域
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权删除区域");
        }
        return areaMapper.deleteById(areaId) > 0;
    }
}


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
        
        wrapper.eq(BaseArea::getStatus, 1)
               .orderByDesc(BaseArea::getCreateTime);
        return areaMapper.selectList(wrapper);
    }
    
    @Override
    public BaseArea getById(Long areaId) {
        return areaMapper.selectById(areaId);
    }
    
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
        
        if (areaName != null && !areaName.isEmpty()) {
            wrapper.like(BaseArea::getAreaName, areaName);
        }
        if (departmentId != null) {
            wrapper.eq(BaseArea::getDepartmentId, departmentId);
        }
        if (status != null) {
            wrapper.eq(BaseArea::getStatus, status);
        }
        
        wrapper.orderByDesc(BaseArea::getCreateTime);
        return areaMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveArea(BaseArea area) {
        // 普通操作员不能新增区域
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权新增区域");
        }
        return areaMapper.insert(area) > 0;
    }
    
    @Override
    public boolean updateArea(BaseArea area) {
        // 普通操作员不能修改区域
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权修改区域");
        }
        return areaMapper.updateById(area) > 0;
    }
    
    @Override
    public boolean deleteArea(Long areaId) {
        // 普通操作员不能删除区域
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权删除区域");
        }
        return areaMapper.deleteById(areaId) > 0;
    }
}


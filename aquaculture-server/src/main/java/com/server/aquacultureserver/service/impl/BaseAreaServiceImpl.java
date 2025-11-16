package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseArea;
import com.server.aquacultureserver.mapper.BaseAreaMapper;
import com.server.aquacultureserver.service.BaseAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return areaMapper.selectList(
            new LambdaQueryWrapper<BaseArea>()
                .eq(BaseArea::getStatus, 1)
                .orderByDesc(BaseArea::getCreateTime)
        );
    }
    
    @Override
    public BaseArea getById(Long areaId) {
        return areaMapper.selectById(areaId);
    }
    
    @Override
    public Page<BaseArea> getPage(Integer current, Integer size, String areaName, Long departmentId, Integer status) {
        Page<BaseArea> page = new Page<>(current, size);
        LambdaQueryWrapper<BaseArea> wrapper = new LambdaQueryWrapper<>();
        
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
        return areaMapper.insert(area) > 0;
    }
    
    @Override
    public boolean updateArea(BaseArea area) {
        return areaMapper.updateById(area) > 0;
    }
    
    @Override
    public boolean deleteArea(Long areaId) {
        return areaMapper.deleteById(areaId) > 0;
    }
}


package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseEquipment;
import com.server.aquacultureserver.mapper.BaseEquipmentMapper;
import com.server.aquacultureserver.service.BaseEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备信息服务实现类
 */
@Service
public class BaseEquipmentServiceImpl implements BaseEquipmentService {
    
    @Autowired
    private BaseEquipmentMapper equipmentMapper;
    
    @Override
    public List<BaseEquipment> getAllEquipments() {
        return equipmentMapper.selectList(
            new LambdaQueryWrapper<BaseEquipment>()
                .orderByDesc(BaseEquipment::getCreateTime)
        );
    }
    
    @Override
    public BaseEquipment getById(Long equipmentId) {
        return equipmentMapper.selectById(equipmentId);
    }
    
    @Override
    public Page<BaseEquipment> getPage(Integer current, Integer size, String equipmentName, Long areaId, String equipmentType) {
        Page<BaseEquipment> page = new Page<>(current, size);
        LambdaQueryWrapper<BaseEquipment> wrapper = new LambdaQueryWrapper<>();
        
        if (equipmentName != null && !equipmentName.isEmpty()) {
            wrapper.like(BaseEquipment::getEquipmentName, equipmentName);
        }
        if (areaId != null) {
            wrapper.eq(BaseEquipment::getAreaId, areaId);
        }
        if (equipmentType != null && !equipmentType.isEmpty()) {
            wrapper.eq(BaseEquipment::getEquipmentType, equipmentType);
        }
        
        wrapper.orderByDesc(BaseEquipment::getCreateTime);
        return equipmentMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveEquipment(BaseEquipment equipment) {
        return equipmentMapper.insert(equipment) > 0;
    }
    
    @Override
    public boolean updateEquipment(BaseEquipment equipment) {
        return equipmentMapper.updateById(equipment) > 0;
    }
    
    @Override
    public boolean deleteEquipment(Long equipmentId) {
        return equipmentMapper.deleteById(equipmentId) > 0;
    }
}


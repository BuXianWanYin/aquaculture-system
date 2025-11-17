package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseEquipment;
import com.server.aquacultureserver.mapper.BaseEquipmentMapper;
import com.server.aquacultureserver.service.BaseEquipmentService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
        LambdaQueryWrapper<BaseEquipment> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的设备
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(BaseEquipment::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下所有区域的设备
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(BaseEquipment::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.orderByDesc(BaseEquipment::getCreateTime);
        return equipmentMapper.selectList(wrapper);
    }
    
    @Override
    public BaseEquipment getById(Long equipmentId) {
        return equipmentMapper.selectById(equipmentId);
    }
    
    @Override
    public Page<BaseEquipment> getPage(Integer current, Integer size, String equipmentName, Long areaId, String equipmentType) {
        Page<BaseEquipment> page = new Page<>(current, size);
        LambdaQueryWrapper<BaseEquipment> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的设备
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(BaseEquipment::getAreaId, userAreaId);
            } else {
                return page; // 返回空结果
            }
        }
        // 部门管理员只能查看其部门下所有区域的设备
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(BaseEquipment::getAreaId, areaIds);
            } else {
                return page; // 返回空结果
            }
        }
        
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
        // 普通操作员只能为自己区域新增设备
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法新增设备");
            }
            // 强制设置为当前用户的区域
            equipment.setAreaId(userAreaId);
        }
        // 部门管理员只能为其部门下区域新增设备
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法新增设备");
            }
            if (equipment.getAreaId() == null || !areaIds.contains(equipment.getAreaId())) {
                throw new RuntimeException("您只能为本部门下区域新增设备");
            }
        }
        return equipmentMapper.insert(equipment) > 0;
    }
    
    @Override
    public boolean updateEquipment(BaseEquipment equipment) {
        // 普通操作员只能修改自己区域的设备
        if (UserContext.isOperator()) {
            BaseEquipment existing = getById(equipment.getEquipmentId());
            if (existing == null) {
                throw new RuntimeException("设备不存在");
            }
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null || !userAreaId.equals(existing.getAreaId())) {
                throw new RuntimeException("您只能修改自己区域的设备");
            }
            // 强制设置为当前用户的区域
            equipment.setAreaId(userAreaId);
        }
        // 部门管理员只能修改其部门下区域的设备
        else if (UserContext.isDepartmentManager()) {
            BaseEquipment existing = getById(equipment.getEquipmentId());
            if (existing == null) {
                throw new RuntimeException("设备不存在");
            }
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty() || !areaIds.contains(existing.getAreaId())) {
                throw new RuntimeException("您只能修改本部门下区域的设备");
            }
            // 确保修改后的区域也在部门范围内
            if (equipment.getAreaId() != null && !areaIds.contains(equipment.getAreaId())) {
                throw new RuntimeException("您只能将设备分配给本部门下的区域");
            }
        }
        return equipmentMapper.updateById(equipment) > 0;
    }
    
    @Override
    public boolean deleteEquipment(Long equipmentId) {
        // 普通操作员只能删除自己区域的设备
        if (UserContext.isOperator()) {
            BaseEquipment equipment = getById(equipmentId);
            if (equipment == null) {
                throw new RuntimeException("设备不存在");
            }
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null || !userAreaId.equals(equipment.getAreaId())) {
                throw new RuntimeException("您只能删除自己区域的设备");
            }
        }
        // 部门管理员只能删除其部门下区域的设备
        else if (UserContext.isDepartmentManager()) {
            BaseEquipment equipment = getById(equipmentId);
            if (equipment == null) {
                throw new RuntimeException("设备不存在");
            }
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty() || !areaIds.contains(equipment.getAreaId())) {
                throw new RuntimeException("您只能删除本部门下区域的设备");
            }
        }
        return equipmentMapper.deleteById(equipmentId) > 0;
    }
}


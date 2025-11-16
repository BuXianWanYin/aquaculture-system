package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.DailyInspection;
import com.server.aquacultureserver.mapper.DailyInspectionMapper;
import com.server.aquacultureserver.service.DailyInspectionService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 日常检查记录服务实现类
 */
@Service
public class DailyInspectionServiceImpl implements DailyInspectionService {
    
    @Autowired
    private DailyInspectionMapper inspectionMapper;
    
    @Override
    public List<DailyInspection> getAllInspections() {
        LambdaQueryWrapper<DailyInspection> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的检查记录
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(DailyInspection::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下所有区域的检查记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(DailyInspection::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.eq(DailyInspection::getStatus, 1);
        wrapper.orderByDesc(DailyInspection::getCreateTime);
        return inspectionMapper.selectList(wrapper);
    }
    
    @Override
    public DailyInspection getById(Long inspectionId) {
        return inspectionMapper.selectById(inspectionId);
    }
    
    @Override
    public Page<DailyInspection> getPage(Integer current, Integer size, Long planId, Long areaId, String inspectionType, String inspectionResult, Integer status) {
        Page<DailyInspection> page = new Page<>(current, size);
        LambdaQueryWrapper<DailyInspection> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的检查记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(DailyInspection::getAreaId, userAreaId);
            } else {
                return page;
            }
        }
        // 部门管理员只能查看其部门下所有区域的检查记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(DailyInspection::getAreaId, areaIds);
            } else {
                return page;
            }
        }
        
        if (planId != null) {
            wrapper.eq(DailyInspection::getPlanId, planId);
        }
        if (areaId != null) {
            wrapper.eq(DailyInspection::getAreaId, areaId);
        }
        if (inspectionType != null && !inspectionType.isEmpty()) {
            wrapper.eq(DailyInspection::getInspectionType, inspectionType);
        }
        if (inspectionResult != null && !inspectionResult.isEmpty()) {
            wrapper.eq(DailyInspection::getInspectionResult, inspectionResult);
        }
        if (status != null) {
            wrapper.eq(DailyInspection::getStatus, status);
        } else {
            wrapper.eq(DailyInspection::getStatus, 1);
        }
        
        wrapper.orderByDesc(DailyInspection::getCreateTime);
        return inspectionMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveInspection(DailyInspection inspection) {
        // 普通操作员只能创建自己区域的检查记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法创建检查记录");
            }
            inspection.setAreaId(userAreaId);
        }
        // 部门管理员只能创建其部门下区域的检查记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法创建检查记录");
            }
            if (inspection.getAreaId() == null || !areaIds.contains(inspection.getAreaId())) {
                throw new RuntimeException("您只能创建本部门下区域的检查记录");
            }
        }
        
        // 新记录默认状态为正常
        if (inspection.getStatus() == null) {
            inspection.setStatus(1);
        }
        
        // 设置创建时间和创建人
        if (inspection.getCreateTime() == null) {
            inspection.setCreateTime(LocalDateTime.now());
        }
        if (inspection.getCreatorId() == null) {
            inspection.setCreatorId(UserContext.getCurrentUserId());
        }
        
        return inspectionMapper.insert(inspection) > 0;
    }
    
    @Override
    public boolean updateInspection(DailyInspection inspection) {
        return inspectionMapper.updateById(inspection) > 0;
    }
    
    @Override
    public boolean deleteInspection(Long inspectionId) {
        // 软删除，将状态设置为0
        DailyInspection inspection = getById(inspectionId);
        if (inspection == null) {
            throw new RuntimeException("检查记录不存在");
        }
        inspection.setStatus(0);
        return inspectionMapper.updateById(inspection) > 0;
    }
    
    @Override
    public long count() {
        LambdaQueryWrapper<DailyInspection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyInspection::getStatus, 1);
        return inspectionMapper.selectCount(wrapper);
    }
}


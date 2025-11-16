package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.DiseaseRecord;
import com.server.aquacultureserver.mapper.DiseaseRecordMapper;
import com.server.aquacultureserver.service.DiseaseRecordService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 病害记录服务实现类
 */
@Service
public class DiseaseRecordServiceImpl implements DiseaseRecordService {
    
    @Autowired
    private DiseaseRecordMapper recordMapper;
    
    @Override
    public List<DiseaseRecord> getAllRecords() {
        LambdaQueryWrapper<DiseaseRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的病害记录
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(DiseaseRecord::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下所有区域的病害记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(DiseaseRecord::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.eq(DiseaseRecord::getStatus, 1);
        wrapper.orderByDesc(DiseaseRecord::getCreateTime);
        return recordMapper.selectList(wrapper);
    }
    
    @Override
    public DiseaseRecord getById(Long recordId) {
        return recordMapper.selectById(recordId);
    }
    
    @Override
    public Page<DiseaseRecord> getPage(Integer current, Integer size, Long planId, Long areaId, String diseaseName, String diseaseType, Integer status) {
        Page<DiseaseRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<DiseaseRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的病害记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(DiseaseRecord::getAreaId, userAreaId);
            } else {
                return page;
            }
        }
        // 部门管理员只能查看其部门下所有区域的病害记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(DiseaseRecord::getAreaId, areaIds);
            } else {
                return page;
            }
        }
        
        if (planId != null) {
            wrapper.eq(DiseaseRecord::getPlanId, planId);
        }
        if (areaId != null) {
            wrapper.eq(DiseaseRecord::getAreaId, areaId);
        }
        if (diseaseName != null && !diseaseName.isEmpty()) {
            wrapper.like(DiseaseRecord::getDiseaseName, diseaseName);
        }
        if (diseaseType != null && !diseaseType.isEmpty()) {
            wrapper.eq(DiseaseRecord::getDiseaseType, diseaseType);
        }
        if (status != null) {
            wrapper.eq(DiseaseRecord::getStatus, status);
        } else {
            wrapper.eq(DiseaseRecord::getStatus, 1);
        }
        
        wrapper.orderByDesc(DiseaseRecord::getCreateTime);
        return recordMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveRecord(DiseaseRecord record) {
        // 普通操作员只能创建自己区域的病害记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法创建病害记录");
            }
            record.setAreaId(userAreaId);
        }
        // 部门管理员只能创建其部门下区域的病害记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法创建病害记录");
            }
            if (record.getAreaId() == null || !areaIds.contains(record.getAreaId())) {
                throw new RuntimeException("您只能创建本部门下区域的病害记录");
            }
        }
        
        // 新记录默认状态为正常
        if (record.getStatus() == null) {
            record.setStatus(1);
        }
        
        // 设置创建时间和创建人
        if (record.getCreateTime() == null) {
            record.setCreateTime(LocalDateTime.now());
        }
        if (record.getCreatorId() == null) {
            record.setCreatorId(UserContext.getCurrentUserId());
        }
        
        return recordMapper.insert(record) > 0;
    }
    
    @Override
    public boolean updateRecord(DiseaseRecord record) {
        return recordMapper.updateById(record) > 0;
    }
    
    @Override
    public boolean deleteRecord(Long recordId) {
        // 软删除，将状态设置为0
        DiseaseRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("病害记录不存在");
        }
        record.setStatus(0);
        return recordMapper.updateById(record) > 0;
    }
    
    @Override
    public long count() {
        LambdaQueryWrapper<DiseaseRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiseaseRecord::getStatus, 1);
        return recordMapper.selectCount(wrapper);
    }
}


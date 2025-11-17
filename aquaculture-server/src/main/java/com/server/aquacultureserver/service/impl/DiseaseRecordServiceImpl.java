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
    
    /**
     * 查询所有有效的病害记录
     * 根据用户角色进行权限过滤：
     * - 普通操作员：只能查看自己区域的病害记录
     * - 部门管理员：只能查看其部门下所有区域的病害记录
     * - 系统管理员：可以查看所有病害记录
     * @return 病害记录列表
     */
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
        
        // 只查询状态为正常（1）的记录
        wrapper.eq(DiseaseRecord::getStatus, 1);
        // 按创建时间倒序排列
        wrapper.orderByDesc(DiseaseRecord::getCreateTime);
        return recordMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询病害记录
     * @param recordId 病害记录ID
     * @return 病害记录
     */
    @Override
    public DiseaseRecord getById(Long recordId) {
        return recordMapper.selectById(recordId);
    }
    
    /**
     * 分页查询病害记录
     * 根据用户角色进行权限过滤
     * @param current 当前页码
     * @param size 每页大小
     * @param planId 养殖计划ID
     * @param areaId 区域ID
     * @param diseaseName 病害名称（模糊查询）
     * @param diseaseType 病害类型（精确查询）
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
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
    
    /**
     * 新增病害记录
     * 根据用户角色进行权限控制：
     * - 普通操作员：只能创建自己区域的病害记录
     * - 部门管理员：只能创建其部门下区域的病害记录
     * @param record 病害记录
     * @return 是否成功
     */
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
        
        // 新记录默认状态为正常（1）
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
    
    /**
     * 更新病害记录
     * @param record 病害记录
     * @return 是否成功
     */
    @Override
    public boolean updateRecord(DiseaseRecord record) {
        return recordMapper.updateById(record) > 0;
    }
    
    /**
     * 删除病害记录（软删除）
     * @param recordId 病害记录ID
     * @return 是否成功
     */
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
    
    /**
     * 统计有效的病害记录数量
     * @return 记录数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<DiseaseRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiseaseRecord::getStatus, 1);
        return recordMapper.selectCount(wrapper);
    }
    
    /**
     * 根据养殖计划ID查询病害记录
     * 用于用药记录中选择病害记录时获取该计划的所有病害记录
     * @param planId 养殖计划ID
     * @return 病害记录列表
     */
    @Override
    public List<DiseaseRecord> getByPlanId(Long planId) {
        LambdaQueryWrapper<DiseaseRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiseaseRecord::getPlanId, planId);
        wrapper.eq(DiseaseRecord::getStatus, 1);
        wrapper.orderByDesc(DiseaseRecord::getCreateTime);
        return recordMapper.selectList(wrapper);
    }
}


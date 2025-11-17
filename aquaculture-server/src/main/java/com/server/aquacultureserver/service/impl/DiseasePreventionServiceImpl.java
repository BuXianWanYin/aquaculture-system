package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.DiseasePrevention;
import com.server.aquacultureserver.mapper.DiseasePreventionMapper;
import com.server.aquacultureserver.service.DiseasePreventionService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 病害防治记录服务实现类
 */
@Service
public class DiseasePreventionServiceImpl implements DiseasePreventionService {
    
    @Autowired
    private DiseasePreventionMapper preventionMapper;
    
    /**
     * 查询所有有效的病害防治记录
     * 根据用户角色进行权限过滤
     * @return 防治记录列表
     */
    @Override
    public List<DiseasePrevention> getAllPreventions() {
        LambdaQueryWrapper<DiseasePrevention> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的防治记录
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(DiseasePrevention::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下所有区域的防治记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(DiseasePrevention::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.eq(DiseasePrevention::getStatus, 1);
        wrapper.orderByDesc(DiseasePrevention::getCreateTime);
        return preventionMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询防治记录
     * @param preventionId 防治记录ID
     * @return 防治记录
     */
    @Override
    public DiseasePrevention getById(Long preventionId) {
        return preventionMapper.selectById(preventionId);
    }
    
    /**
     * 分页查询病害防治记录
     * 根据用户角色进行权限过滤
     * @param current 当前页码
     * @param size 每页大小
     * @param recordId 病害记录ID
     * @param planId 养殖计划ID
     * @param areaId 区域ID
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
    @Override
    public Page<DiseasePrevention> getPage(Integer current, Integer size, Long recordId, Long planId, Long areaId, Integer status) {
        Page<DiseasePrevention> page = new Page<>(current, size);
        LambdaQueryWrapper<DiseasePrevention> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的防治记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(DiseasePrevention::getAreaId, userAreaId);
            } else {
                return page;
            }
        }
        // 部门管理员只能查看其部门下所有区域的防治记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(DiseasePrevention::getAreaId, areaIds);
            } else {
                return page;
            }
        }
        
        if (recordId != null) {
            wrapper.eq(DiseasePrevention::getRecordId, recordId);
        }
        if (planId != null) {
            wrapper.eq(DiseasePrevention::getPlanId, planId);
        }
        if (areaId != null) {
            wrapper.eq(DiseasePrevention::getAreaId, areaId);
        }
        if (status != null) {
            wrapper.eq(DiseasePrevention::getStatus, status);
        } else {
            wrapper.eq(DiseasePrevention::getStatus, 1);
        }
        
        wrapper.orderByDesc(DiseasePrevention::getCreateTime);
        return preventionMapper.selectPage(page, wrapper);
    }
    
    /**
     * 根据病害记录ID查询防治记录
     * @param recordId 病害记录ID
     * @return 防治记录列表
     */
    @Override
    public List<DiseasePrevention> getByRecordId(Long recordId) {
        LambdaQueryWrapper<DiseasePrevention> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiseasePrevention::getRecordId, recordId);
        wrapper.eq(DiseasePrevention::getStatus, 1);
        wrapper.orderByDesc(DiseasePrevention::getCreateTime);
        return preventionMapper.selectList(wrapper);
    }
    
    /**
     * 新增病害防治记录
     * 根据用户角色进行权限控制
     * @param prevention 防治记录
     * @return 是否成功
     */
    @Override
    public boolean savePrevention(DiseasePrevention prevention) {
        // 普通操作员只能创建自己区域的防治记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法创建防治记录");
            }
            prevention.setAreaId(userAreaId);
        }
        // 部门管理员只能创建其部门下区域的防治记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法创建防治记录");
            }
            if (prevention.getAreaId() == null || !areaIds.contains(prevention.getAreaId())) {
                throw new RuntimeException("您只能创建本部门下区域的防治记录");
            }
        }
        
        // 新记录默认状态为正常
        if (prevention.getStatus() == null) {
            prevention.setStatus(1);
        }
        
        // 设置创建时间和创建人
        if (prevention.getCreateTime() == null) {
            prevention.setCreateTime(LocalDateTime.now());
        }
        if (prevention.getCreatorId() == null) {
            prevention.setCreatorId(UserContext.getCurrentUserId());
        }
        
        return preventionMapper.insert(prevention) > 0;
    }
    
    /**
     * 更新病害防治记录
     * @param prevention 防治记录
     * @return 是否成功
     */
    @Override
    public boolean updatePrevention(DiseasePrevention prevention) {
        return preventionMapper.updateById(prevention) > 0;
    }
    
    /**
     * 删除病害防治记录（软删除）
     * @param preventionId 防治记录ID
     * @return 是否成功
     */
    @Override
    public boolean deletePrevention(Long preventionId) {
        // 软删除，将状态设置为0
        DiseasePrevention prevention = getById(preventionId);
        if (prevention == null) {
            throw new RuntimeException("防治记录不存在");
        }
        prevention.setStatus(0);
        return preventionMapper.updateById(prevention) > 0;
    }
    
    /**
     * 统计有效的病害防治记录数量
     * @return 记录数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<DiseasePrevention> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiseasePrevention::getStatus, 1);
        return preventionMapper.selectCount(wrapper);
    }
}


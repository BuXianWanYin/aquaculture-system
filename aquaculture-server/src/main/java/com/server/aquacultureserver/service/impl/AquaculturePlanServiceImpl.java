package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.AquaculturePlan;
import com.server.aquacultureserver.mapper.AquaculturePlanMapper;
import com.server.aquacultureserver.service.AquaculturePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 养殖计划服务实现类
 */
@Service
public class AquaculturePlanServiceImpl implements AquaculturePlanService {
    
    @Autowired
    private AquaculturePlanMapper planMapper;
    
    @Override
    public List<AquaculturePlan> getAllPlans() {
        return planMapper.selectList(
            new LambdaQueryWrapper<AquaculturePlan>()
                .orderByDesc(AquaculturePlan::getCreateTime)
        );
    }
    
    @Override
    public AquaculturePlan getById(Long planId) {
        return planMapper.selectById(planId);
    }
    
    @Override
    public Page<AquaculturePlan> getPage(Integer current, Integer size, String planName, Long areaId, Long breedId, Integer status) {
        Page<AquaculturePlan> page = new Page<>(current, size);
        LambdaQueryWrapper<AquaculturePlan> wrapper = new LambdaQueryWrapper<>();
        
        if (planName != null && !planName.isEmpty()) {
            wrapper.like(AquaculturePlan::getPlanName, planName);
        }
        if (areaId != null) {
            wrapper.eq(AquaculturePlan::getAreaId, areaId);
        }
        if (breedId != null) {
            wrapper.eq(AquaculturePlan::getBreedId, breedId);
        }
        if (status != null) {
            wrapper.eq(AquaculturePlan::getStatus, status);
        }
        
        wrapper.orderByDesc(AquaculturePlan::getCreateTime);
        return planMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean savePlan(AquaculturePlan plan) {
        // 新计划默认状态为待审批
        if (plan.getStatus() == null) {
            plan.setStatus(0);
        }
        return planMapper.insert(plan) > 0;
    }
    
    @Override
    public boolean updatePlan(AquaculturePlan plan) {
        return planMapper.updateById(plan) > 0;
    }
    
    @Override
    public boolean deletePlan(Long planId) {
        return planMapper.deleteById(planId) > 0;
    }
    
    @Override
    public boolean approvePlan(Long planId, Long approverId, String approveOpinion, Integer status) {
        AquaculturePlan plan = getById(planId);
        if (plan == null) {
            throw new RuntimeException("计划不存在");
        }
        
        plan.setApproverId(approverId);
        plan.setApproveOpinion(approveOpinion);
        plan.setApproveTime(LocalDateTime.now());
        plan.setStatus(status);
        
        // 如果审批通过，状态改为执行中
        if (status == 1) {
            plan.setStatus(3);
        }
        
        return planMapper.updateById(plan) > 0;
    }
    
    @Override
    public long count() {
        return planMapper.selectCount(null);
    }
    
    @Override
    public long countPendingPlans() {
        return planMapper.selectCount(
            new LambdaQueryWrapper<AquaculturePlan>()
                .eq(AquaculturePlan::getStatus, 0)
        );
    }
}


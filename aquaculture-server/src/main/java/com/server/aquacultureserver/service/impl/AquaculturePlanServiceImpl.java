package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.AquaculturePlan;
import com.server.aquacultureserver.mapper.AquaculturePlanMapper;
import com.server.aquacultureserver.service.AquaculturePlanService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
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
        LambdaQueryWrapper<AquaculturePlan> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的计划
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(AquaculturePlan::getAreaId, areaId);
            } else {
                // 如果没有分配区域，返回空列表
                return Collections.emptyList();
            }
        }
        
        wrapper.orderByDesc(AquaculturePlan::getCreateTime);
        return planMapper.selectList(wrapper);
    }
    
    @Override
    public AquaculturePlan getById(Long planId) {
        return planMapper.selectById(planId);
    }
    
    @Override
    public Page<AquaculturePlan> getPage(Integer current, Integer size, String planName, Long areaId, Long breedId, Integer status) {
        Page<AquaculturePlan> page = new Page<>(current, size);
        LambdaQueryWrapper<AquaculturePlan> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的计划
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(AquaculturePlan::getAreaId, userAreaId);
            } else {
                // 如果没有分配区域，返回空结果
                return page;
            }
        }
        
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
        // 普通操作员只能创建自己区域的计划
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法创建计划");
            }
            // 强制设置为当前用户的区域
            plan.setAreaId(userAreaId);
        }
        
        // 新计划默认状态为待审批
        if (plan.getStatus() == null) {
            plan.setStatus(0);
        }
        return planMapper.insert(plan) > 0;
    }
    
    @Override
    public boolean updatePlan(AquaculturePlan plan) {
        // 普通操作员只能修改自己区域的计划
        if (UserContext.isOperator()) {
            AquaculturePlan existingPlan = getById(plan.getPlanId());
            if (existingPlan == null) {
                throw new RuntimeException("计划不存在");
            }
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null || !userAreaId.equals(existingPlan.getAreaId())) {
                throw new RuntimeException("您只能修改自己区域的计划");
            }
            // 不允许修改区域
            plan.setAreaId(userAreaId);
        }
        return planMapper.updateById(plan) > 0;
    }
    
    @Override
    public boolean deletePlan(Long planId) {
        // 普通操作员只能删除自己区域的计划
        if (UserContext.isOperator()) {
            AquaculturePlan plan = getById(planId);
            if (plan == null) {
                throw new RuntimeException("计划不存在");
            }
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null || !userAreaId.equals(plan.getAreaId())) {
                throw new RuntimeException("您只能删除自己区域的计划");
            }
        }
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


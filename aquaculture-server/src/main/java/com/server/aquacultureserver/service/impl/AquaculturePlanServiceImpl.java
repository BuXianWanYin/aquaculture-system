package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.AquaculturePlan;
import com.server.aquacultureserver.domain.YieldStatistics;
import com.server.aquacultureserver.mapper.AquaculturePlanMapper;
import com.server.aquacultureserver.mapper.YieldStatisticsMapper;
import com.server.aquacultureserver.service.AquaculturePlanService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    
    @Autowired
    private YieldStatisticsMapper yieldStatisticsMapper;
    
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
        // 部门管理员只能查看其部门下所有区域的计划
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(AquaculturePlan::getAreaId, areaIds);
            } else {
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
        // 部门管理员只能查看其部门下所有区域的计划
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(AquaculturePlan::getAreaId, areaIds);
            } else {
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
        // 部门管理员只能创建其部门下区域的计划
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法创建计划");
            }
            if (plan.getAreaId() == null || !areaIds.contains(plan.getAreaId())) {
                throw new RuntimeException("您只能创建本部门下区域的计划");
            }
        }
        
        // 新计划默认状态为待审批
        if (plan.getStatus() == null) {
            plan.setStatus(0);
        }
        
        // 自动计算结束日期：开始日期 + 周期天数
        if (plan.getStartDate() != null && plan.getCycleDays() != null && plan.getCycleDays() > 0) {
            plan.setEndDate(plan.getStartDate().plusDays(plan.getCycleDays()));
        }
        
        boolean result = planMapper.insert(plan) > 0;
        
        // 保存后检查状态（如果开始日期已到达）
        if (result && plan.getPlanId() != null) {
            checkAndUpdatePlanStatus(plan.getPlanId());
        }
        
        return result;
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
        // 部门管理员只能修改其部门下区域的计划
        else if (UserContext.isDepartmentManager()) {
            AquaculturePlan existingPlan = getById(plan.getPlanId());
            if (existingPlan == null) {
                throw new RuntimeException("计划不存在");
            }
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || !areaIds.contains(existingPlan.getAreaId())) {
                throw new RuntimeException("您只能修改本部门下区域的计划");
            }
            // 不允许修改到其他部门
            if (plan.getAreaId() != null && !areaIds.contains(plan.getAreaId())) {
                throw new RuntimeException("您只能将计划修改为本部门下的区域");
            }
        }
        
        // 自动计算结束日期：开始日期 + 周期天数
        if (plan.getStartDate() != null && plan.getCycleDays() != null && plan.getCycleDays() > 0) {
            plan.setEndDate(plan.getStartDate().plusDays(plan.getCycleDays()));
        }
        
        boolean result = planMapper.updateById(plan) > 0;
        
        // 更新后检查状态
        if (result && plan.getPlanId() != null) {
            checkAndUpdatePlanStatus(plan.getPlanId());
        }
        
        return result;
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
        // 部门管理员只能删除其部门下区域的计划
        else if (UserContext.isDepartmentManager()) {
            AquaculturePlan plan = getById(planId);
            if (plan == null) {
                throw new RuntimeException("计划不存在");
            }
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || !areaIds.contains(plan.getAreaId())) {
                throw new RuntimeException("您只能删除本部门下区域的计划");
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
    
    /**
     * 检查并更新计划状态（基于日期和产量）
     * 开始日期到达 → 自动改为"执行中"（3）
     * 结束日期到达且累计产量 ≥ 目标产量 → 改为"已完成"（4）
     * 结束日期到达但产量未达标 → 保持"执行中"
     */
    @Transactional
    public void checkAndUpdatePlanStatus(Long planId) {
        AquaculturePlan plan = getById(planId);
        if (plan == null) {
            return;
        }
        
        LocalDate today = LocalDate.now();
        boolean needUpdate = false;
        
        // 1. 如果计划已通过审批（status=1）且开始日期已到达，改为执行中
        if (plan.getStatus() == 1 && plan.getStartDate() != null && 
            !today.isBefore(plan.getStartDate())) {
            plan.setStatus(3);
            needUpdate = true;
        }
        
        // 2. 如果计划在执行中（status=3），检查是否应该完成
        if (plan.getStatus() == 3 && plan.getEndDate() != null) {
            // 计算累计实际产量
            BigDecimal totalYield = calculateTotalYield(planId);
            
            // 如果结束日期已到达
            if (!today.isBefore(plan.getEndDate())) {
                // 如果累计产量 >= 目标产量，改为已完成
                if (plan.getTargetYield() != null && totalYield != null &&
                    totalYield.compareTo(plan.getTargetYield()) >= 0) {
                    plan.setStatus(4);
                    needUpdate = true;
                }
                // 否则保持执行中状态（产量未达标）
            }
        }
        
        if (needUpdate) {
            planMapper.updateById(plan);
        }
    }
    
    /**
     * 计算计划的累计实际产量（只统计已审核通过的产量）
     */
    public BigDecimal calculateTotalYield(Long planId) {
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YieldStatistics::getPlanId, planId)
               .eq(YieldStatistics::getStatus, 1); // 只统计已审核通过的产量
        
        List<YieldStatistics> statistics = yieldStatisticsMapper.selectList(wrapper);
        
        BigDecimal total = BigDecimal.ZERO;
        for (YieldStatistics stat : statistics) {
            if (stat.getActualYield() != null) {
                total = total.add(stat.getActualYield());
            }
        }
        return total;
    }
    
    /**
     * 计算计划完成率（累计实际产量 / 目标产量 × 100%）
     */
    public Double calculateCompletionRate(Long planId) {
        AquaculturePlan plan = getById(planId);
        if (plan == null || plan.getTargetYield() == null || 
            plan.getTargetYield().compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        BigDecimal totalYield = calculateTotalYield(planId);
        if (totalYield == null || totalYield.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        // 计算完成率，保留2位小数
        double rate = totalYield.divide(plan.getTargetYield(), 4, BigDecimal.ROUND_HALF_UP)
                               .multiply(new BigDecimal(100))
                               .doubleValue();
        
        // 完成率不能超过100%
        return Math.min(rate, 100.0);
    }
}


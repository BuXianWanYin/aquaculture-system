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
    
    @Autowired
    private com.server.aquacultureserver.service.FeedUsageService feedUsageService;
    
    /**
     * 查询所有养殖计划
     * 根据用户角色进行权限过滤
     * @return 养殖计划列表
     */
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
    
    /**
     * 查询已审核通过的养殖计划
     * 只返回状态不为待审批（0）和已驳回（2）的计划，即已通过（1）、执行中（3）、已完成（4）、已取消（5）
     * 根据用户角色进行权限过滤
     * @return 已审核通过的养殖计划列表
     */
    @Override
    public List<AquaculturePlan> getApprovedPlans() {
        LambdaQueryWrapper<AquaculturePlan> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询已审核通过的计划：status != 0 (待审批) && status != 2 (已驳回)
        wrapper.ne(AquaculturePlan::getStatus, 0);
        wrapper.ne(AquaculturePlan::getStatus, 2);
        
        // 普通操作员只能查看自己区域的计划
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(AquaculturePlan::getAreaId, areaId);
            } else {
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
    
    /**
     * 根据ID查询养殖计划
     * @param planId 计划ID
     * @return 养殖计划
     */
    @Override
    public AquaculturePlan getById(Long planId) {
        return planMapper.selectById(planId);
    }
    
    /**
     * 分页查询养殖计划
     * 根据用户角色进行权限过滤，并自动计算每个计划的饲料已使用金额
     * @param current 当前页码
     * @param size 每页大小
     * @param planName 计划名称（模糊查询）
     * @param areaId 区域ID
     * @param breedId 品种ID
     * @param status 状态（0-待审批，1-已通过，2-已驳回，3-执行中，4-已完成，5-已取消）
     * @return 分页结果（包含饲料已使用金额）
     */
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
        Page<AquaculturePlan> result = planMapper.selectPage(page, wrapper);
        
        // 为每个计划计算并设置饲料已使用金额
        for (AquaculturePlan plan : result.getRecords()) {
            if (plan.getPlanId() != null) {
                java.math.BigDecimal usedAmount = feedUsageService.calculateTotalCostByPlanId(plan.getPlanId());
                plan.setFeedUsedAmount(usedAmount);
            }
        }
        
        return result;
    }
    
    /**
     * 新增养殖计划
     * 根据用户角色进行权限控制，自动计算结束日期
     * @param plan 养殖计划
     * @return 是否成功
     */
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
        
        // 新计划默认状态为待审批（0）
        if (plan.getStatus() == null) {
            plan.setStatus(0);
        }
        
        // 自动计算结束日期：开始日期 + 周期天数
        if (plan.getStartDate() != null && plan.getCycleDays() != null && plan.getCycleDays() > 0) {
            plan.setEndDate(plan.getStartDate().plusDays(plan.getCycleDays()));
        }
        
        boolean result = planMapper.insert(plan) > 0;
        
        return result;
    }
    
    /**
     * 更新养殖计划
     * 根据用户角色进行权限控制，自动重新计算结束日期
     * 注意：状态修改需要满足特定条件（只有已通过的计划才能修改为执行中或已完成）
     * @param plan 养殖计划
     * @return 是否成功
     */
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
        
        return result;
    }
    
    /**
     * 删除养殖计划（物理删除）
     * 根据用户角色进行权限控制
     * @param planId 计划ID
     * @return 是否成功
     */
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
    
    /**
     * 审批养殖计划
     * 如果审批通过（status=1），计划状态会自动设置为执行中（3）
     * @param planId 计划ID
     * @param approverId 审批人ID
     * @param approveOpinion 审批意见
     * @param status 审批状态（1-通过，0-驳回）
     * @return 是否成功
     */
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
        
        // 如果审批通过，状态改为执行中（3）
        if (status == 1) {
            plan.setStatus(3);
        }
        
        return planMapper.updateById(plan) > 0;
    }
    
    /**
     * 统计所有养殖计划数量
     * @return 计划数量
     */
    @Override
    public long count() {
        return planMapper.selectCount(null);
    }
    
    /**
     * 统计待审批的养殖计划数量
     * @return 待审批计划数量
     */
    @Override
    public long countPendingPlans() {
        return planMapper.selectCount(
            new LambdaQueryWrapper<AquaculturePlan>()
                .eq(AquaculturePlan::getStatus, 0)
        );
    }
    
    /**
     * 检查并更新计划状态（基于日期和产量）
     * 注意：自动状态转换规则已移除，状态需要手动管理
     * 保留此方法以便将来可能需要恢复自动状态转换功能
     * @param planId 计划ID
     */
    @Transactional
    public void checkAndUpdatePlanStatus(Long planId) {
        // 自动状态转换规则已移除
        // 状态转换现在完全由用户手动控制
    }
    
    /**
     * 计算计划的累计实际产量
     * 只统计已审核通过的产量统计记录
     * @param planId 计划ID
     * @return 累计实际产量
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
     * 计算计划完成率
     * 计算公式：累计实际产量 / 目标产量 × 100%
     * 完成率最高为100%
     * @param planId 计划ID
     * @return 完成率（百分比，0.0-100.0）
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
        
        // 计算完成率，保留4位小数精度
        double rate = totalYield.divide(plan.getTargetYield(), 4, BigDecimal.ROUND_HALF_UP)
                               .multiply(new BigDecimal(100))
                               .doubleValue();
        
        // 完成率不能超过100%
        return Math.min(rate, 100.0);
    }
}


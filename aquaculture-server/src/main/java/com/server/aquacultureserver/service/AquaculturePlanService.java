package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.AquaculturePlan;

import java.util.List;

/**
 * 养殖计划服务接口
 */
public interface AquaculturePlanService {
    
    /**
     * 查询所有计划
     */
    List<AquaculturePlan> getAllPlans();
    
    /**
     * 查询已审核通过的计划（用于下拉选择）
     * status != 0 (待审批) && status != 2 (已驳回)
     */
    List<AquaculturePlan> getApprovedPlans();
    
    /**
     * 根据ID查询计划
     */
    AquaculturePlan getById(Long planId);
    
    /**
     * 分页查询计划列表
     */
    Page<AquaculturePlan> getPage(Integer current, Integer size, String planName, Long areaId, Long breedId, Integer status);
    
    /**
     * 新增计划
     */
    boolean savePlan(AquaculturePlan plan);
    
    /**
     * 更新计划
     */
    boolean updatePlan(AquaculturePlan plan);
    
    /**
     * 删除计划
     */
    boolean deletePlan(Long planId);
    
    /**
     * 审批计划
     */
    boolean approvePlan(Long planId, Long approverId, String approveOpinion, Integer status);
    
    /**
     * 统计计划总数
     */
    long count();
    
    /**
     * 统计待审批计划数
     */
    long countPendingPlans();
    
    /**
     * 检查并更新计划状态（基于日期和产量）
     */
    void checkAndUpdatePlanStatus(Long planId);
    
    /**
     * 计算计划的累计实际产量
     */
    java.math.BigDecimal calculateTotalYield(Long planId);
    
    /**
     * 计算计划完成率（累计实际产量 / 目标产量 × 100%）
     */
    Double calculateCompletionRate(Long planId);
}


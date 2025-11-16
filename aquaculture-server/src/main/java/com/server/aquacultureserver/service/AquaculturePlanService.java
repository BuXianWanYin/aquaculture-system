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
}


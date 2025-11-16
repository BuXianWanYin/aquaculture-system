package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.PlanAdjust;

import java.util.List;

/**
 * 计划调整服务接口
 */
public interface PlanAdjustService {
    
    /**
     * 查询所有调整记录
     */
    List<PlanAdjust> getAllAdjusts();
    
    /**
     * 根据ID查询调整记录
     */
    PlanAdjust getById(Long adjustId);
    
    /**
     * 根据计划ID查询调整记录
     */
    List<PlanAdjust> getByPlanId(Long planId);
    
    /**
     * 分页查询调整记录列表
     */
    Page<PlanAdjust> getPage(Integer current, Integer size, Long planId, Integer approveStatus);
    
    /**
     * 新增调整申请
     */
    boolean saveAdjust(PlanAdjust adjust);
    
    /**
     * 更新调整记录
     */
    boolean updateAdjust(PlanAdjust adjust);
    
    /**
     * 审批调整申请
     */
    boolean approveAdjust(Long adjustId, Long approverId, String approveOpinion, Integer approveStatus);
}


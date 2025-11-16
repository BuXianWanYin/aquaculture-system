package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.PlanAdjust;
import com.server.aquacultureserver.service.PlanAdjustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 计划调整控制器
 */
@RestController
@RequestMapping("/api/planAdjust")
@CrossOrigin
public class PlanAdjustController {
    
    @Autowired
    private PlanAdjustService adjustService;
    
    /**
     * 根据计划ID查询调整记录
     */
    @GetMapping("/plan/{planId}")
    public Result<List<PlanAdjust>> getByPlanId(@PathVariable Long planId) {
        List<PlanAdjust> adjusts = adjustService.getByPlanId(planId);
        return Result.success(adjusts);
    }
    
    /**
     * 分页查询调整记录列表
     */
    @GetMapping("/page")
    public Result<Page<PlanAdjust>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Integer approveStatus) {
        Page<PlanAdjust> page = adjustService.getPage(current, size, planId, approveStatus);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询调整记录
     */
    @GetMapping("/{adjustId}")
    public Result<PlanAdjust> getById(@PathVariable Long adjustId) {
        PlanAdjust adjust = adjustService.getById(adjustId);
        return Result.success(adjust);
    }
    
    /**
     * 新增调整申请
     */
    @PostMapping
    public Result<Boolean> saveAdjust(@RequestBody PlanAdjust adjust) {
        try {
            boolean success = adjustService.saveAdjust(adjust);
            return Result.success("提交成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新调整记录
     */
    @PutMapping
    public Result<Boolean> updateAdjust(@RequestBody PlanAdjust adjust) {
        try {
            boolean success = adjustService.updateAdjust(adjust);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审批调整申请
     */
    @PostMapping("/approve")
    public Result<Boolean> approveAdjust(@RequestBody Map<String, Object> params) {
        try {
            Long adjustId = Long.valueOf(params.get("adjustId").toString());
            Long approverId = Long.valueOf(params.get("approverId").toString());
            String approveOpinion = params.get("approveOpinion") != null ? params.get("approveOpinion").toString() : "";
            Integer approveStatus = Integer.valueOf(params.get("approveStatus").toString());
            
            boolean success = adjustService.approveAdjust(adjustId, approverId, approveOpinion, approveStatus);
            return Result.success("审批成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


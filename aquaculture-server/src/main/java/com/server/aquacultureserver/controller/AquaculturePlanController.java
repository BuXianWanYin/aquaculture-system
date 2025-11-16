package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresRole;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.AquaculturePlan;
import com.server.aquacultureserver.service.AquaculturePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 养殖计划控制器
 */
@RestController
@RequestMapping("/api/plan")
@CrossOrigin
public class AquaculturePlanController {
    
    @Autowired
    private AquaculturePlanService planService;
    
    /**
     * 查询所有计划
     */
    @GetMapping("/all")
    public Result<List<AquaculturePlan>> getAllPlans() {
        List<AquaculturePlan> plans = planService.getAllPlans();
        return Result.success(plans);
    }
    
    /**
     * 分页查询计划列表
     */
    @GetMapping("/page")
    public Result<Page<AquaculturePlan>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String planName,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Long breedId,
            @RequestParam(required = false) Integer status) {
        Page<AquaculturePlan> page = planService.getPage(current, size, planName, areaId, breedId, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询计划
     */
    @GetMapping("/{planId}")
    public Result<AquaculturePlan> getById(@PathVariable Long planId) {
        AquaculturePlan plan = planService.getById(planId);
        return Result.success(plan);
    }
    
    /**
     * 新增计划
     */
    @PostMapping
    @RequiresRole({1, 2, 3}) // 系统管理员、部门管理员、普通操作员
    public Result<Boolean> savePlan(@RequestBody AquaculturePlan plan) {
        try {
            boolean success = planService.savePlan(plan);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新计划
     */
    @PutMapping
    @RequiresRole({1, 2, 3}) // 系统管理员、部门管理员、普通操作员
    public Result<Boolean> updatePlan(@RequestBody AquaculturePlan plan) {
        try {
            boolean success = planService.updatePlan(plan);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除计划
     */
    @DeleteMapping("/{planId}")
    @RequiresRole({1, 2}) // 系统管理员、部门管理员
    public Result<Boolean> deletePlan(@PathVariable Long planId) {
        try {
            boolean success = planService.deletePlan(planId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审批计划
     */
    @PostMapping("/approve")
    @RequiresRole({1, 2}) // 系统管理员、部门管理员
    public Result<Boolean> approvePlan(@RequestBody Map<String, Object> params) {
        try {
            Long planId = Long.valueOf(params.get("planId").toString());
            Long approverId = Long.valueOf(params.get("approverId").toString());
            String approveOpinion = params.get("approveOpinion") != null ? params.get("approveOpinion").toString() : "";
            Integer status = Integer.valueOf(params.get("status").toString());
            
            boolean success = planService.approvePlan(planId, approverId, approveOpinion, status);
            return Result.success("审批成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


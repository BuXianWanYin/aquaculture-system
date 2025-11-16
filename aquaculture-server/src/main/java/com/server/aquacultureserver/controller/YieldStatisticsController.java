package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresRole;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.YieldStatistics;
import com.server.aquacultureserver.service.YieldStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 产量统计控制器
 */
@RestController
@RequestMapping("/api/yield")
@CrossOrigin
public class YieldStatisticsController {
    
    @Autowired
    private YieldStatisticsService statisticsService;
    
    /**
     * 查询所有产量统计
     */
    @GetMapping("/all")
    public Result<List<YieldStatistics>> getAllStatistics() {
        List<YieldStatistics> statistics = statisticsService.getAllStatistics();
        return Result.success(statistics);
    }
    
    /**
     * 分页查询产量统计列表
     */
    @GetMapping("/page")
    public Result<Page<YieldStatistics>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Long breedId,
            @RequestParam(required = false) Integer status) {
        Page<YieldStatistics> page = statisticsService.getPage(current, size, planId, areaId, breedId, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询产量统计
     */
    @GetMapping("/{yieldId}")
    public Result<YieldStatistics> getById(@PathVariable Long yieldId) {
        YieldStatistics statistics = statisticsService.getById(yieldId);
        return Result.success(statistics);
    }
    
    /**
     * 新增产量统计
     */
    @PostMapping
    @RequiresRole({1, 2, 3}) // 系统管理员、部门管理员、普通操作员
    public Result<YieldStatistics> saveStatistics(@RequestBody YieldStatistics statistics) {
        try {
            boolean success = statisticsService.saveStatistics(statistics);
            if (success) {
                // 返回保存后的对象（包含自动生成的yieldId）
                return Result.success("新增成功", statistics);
            } else {
                return Result.error("新增失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新产量统计
     */
    @PutMapping
    @RequiresRole({1, 2}) // 系统管理员、部门管理员（普通操作员不能编辑）
    public Result<Boolean> updateStatistics(@RequestBody YieldStatistics statistics) {
        try {
            boolean success = statisticsService.updateStatistics(statistics);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除产量统计
     */
    @DeleteMapping("/{yieldId}")
    @RequiresRole({1, 2}) // 系统管理员、部门管理员
    public Result<Boolean> deleteStatistics(@PathVariable Long yieldId) {
        try {
            boolean success = statisticsService.deleteStatistics(yieldId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审核产量统计
     */
    @PostMapping("/audit")
    @RequiresRole({1, 2}) // 系统管理员、部门管理员
    public Result<Boolean> auditStatistics(@RequestBody Map<String, Object> params) {
        try {
            Long yieldId = Long.valueOf(params.get("yieldId").toString());
            Long auditorId = Long.valueOf(params.get("auditorId").toString());
            String auditOpinion = params.get("auditOpinion") != null ? params.get("auditOpinion").toString() : "";
            Integer status = Integer.valueOf(params.get("status").toString());
            
            boolean success = statisticsService.auditStatistics(yieldId, auditorId, auditOpinion, status);
            return Result.success("审核成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


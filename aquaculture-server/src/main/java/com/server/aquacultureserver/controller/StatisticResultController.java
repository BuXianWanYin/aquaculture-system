package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.StatisticResult;
import com.server.aquacultureserver.service.StatisticResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计结果控制器
 */
@RestController
@RequestMapping("/api/statistic")
@CrossOrigin
public class StatisticResultController {
    
    @Autowired
    private StatisticResultService statisticService;
    
    /**
     * 分页查询统计结果列表
     */
    @GetMapping("/page")
    @RequiresPermission({"statistics:list"}) // 需要统计列表查看权限
    public Result<Page<StatisticResult>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String statisticName,
            @RequestParam(required = false) String statisticDimension,
            @RequestParam(required = false) String dimensionValue,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<StatisticResult> page = statisticService.getPage(current, size, statisticName, 
                statisticDimension, dimensionValue, startDate, endDate);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询统计结果详情
     */
    @GetMapping("/{statisticId}")
    @RequiresPermission({"statistics:list"}) // 需要统计列表查看权限
    public Result<StatisticResult> getById(@PathVariable Long statisticId) {
        StatisticResult statistic = statisticService.getById(statisticId);
        return Result.success(statistic);
    }
    
    /**
     * 新增统计结果
     */
    @PostMapping
    @RequiresPermission({"statistics:list"}) // 需要统计列表查看权限（新增统计需要列表权限）
    public Result<Boolean> saveStatistic(@RequestBody StatisticResult statistic) {
        try {
            boolean success = statisticService.saveStatistic(statistic);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新统计结果
     */
    @PutMapping
    @RequiresPermission({"statistics:list"}) // 需要统计列表查看权限
    public Result<Boolean> updateStatistic(@RequestBody StatisticResult statistic) {
        try {
            boolean success = statisticService.updateStatistic(statistic);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除统计结果
     */
    @DeleteMapping("/{statisticId}")
    @RequiresPermission({"statistics:list"}) // 需要统计列表查看权限
    public Result<Boolean> deleteStatistic(@PathVariable Long statisticId) {
        try {
            boolean success = statisticService.deleteStatistic(statisticId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除统计结果
     */
    @DeleteMapping("/batch")
    @RequiresPermission({"statistics:list"}) // 需要统计列表查看权限
    public Result<Boolean> deleteBatch(@RequestBody Long[] statisticIds) {
        try {
            boolean success = statisticService.deleteBatch(statisticIds);
            return Result.success("批量删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取月度产量趋势数据
     */
    @GetMapping("/monthlyYieldTrend")
    @RequiresPermission({"statistics:report"}) // 需要统计报表权限
    public Result<List<Map<String, Object>>> getMonthlyYieldTrend(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> data = statisticService.getMonthlyYieldTrend(startDate, endDate);
        return Result.success(data);
    }
    
    /**
     * 获取品种产量占比数据
     */
    @GetMapping("/breedYieldRatio")
    @RequiresPermission({"statistics:report"}) // 需要统计报表权限
    public Result<List<Map<String, Object>>> getBreedYieldRatio(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> data = statisticService.getBreedYieldRatio(startDate, endDate);
        return Result.success(data);
    }
    
    /**
     * 获取区域产量对比数据
     */
    @GetMapping("/areaYieldComparison")
    @RequiresPermission({"statistics:report"}) // 需要统计报表权限
    public Result<List<Map<String, Object>>> getAreaYieldComparison(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> data = statisticService.getAreaYieldComparison(startDate, endDate);
        return Result.success(data);
    }
    
    /**
     * 获取计划完成情况统计
     */
    @GetMapping("/planCompletionStats")
    @RequiresPermission({"statistics:report"}) // 需要统计报表权限
    public Result<Map<String, Object>> getPlanCompletionStats() {
        Map<String, Object> data = statisticService.getPlanCompletionStats();
        return Result.success(data);
    }
    
    /**
     * 获取部门产量对比数据
     */
    @GetMapping("/departmentYieldComparison")
    @RequiresPermission({"statistics:report"}) // 需要统计报表权限
    public Result<List<Map<String, Object>>> getDepartmentYieldComparison(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> data = statisticService.getDepartmentYieldComparison(startDate, endDate);
        return Result.success(data);
    }
}


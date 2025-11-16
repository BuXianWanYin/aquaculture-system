package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.DailyInspection;
import com.server.aquacultureserver.service.DailyInspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日常检查记录控制器
 */
@RestController
@RequestMapping("/api/production/inspection")
@CrossOrigin
public class DailyInspectionController {
    
    @Autowired
    private DailyInspectionService inspectionService;
    
    /**
     * 查询所有检查记录
     */
    @GetMapping("/all")
    public Result<List<DailyInspection>> getAllInspections() {
        List<DailyInspection> inspections = inspectionService.getAllInspections();
        return Result.success(inspections);
    }
    
    /**
     * 分页查询检查记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"production:inspection:list"})
    public Result<Page<DailyInspection>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String inspectionType,
            @RequestParam(required = false) String inspectionResult,
            @RequestParam(required = false) Integer status) {
        Page<DailyInspection> page = inspectionService.getPage(current, size, planId, areaId, inspectionType, inspectionResult, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询检查记录
     */
    @GetMapping("/{inspectionId}")
    @RequiresPermission({"production:inspection:detail"})
    public Result<DailyInspection> getById(@PathVariable Long inspectionId) {
        DailyInspection inspection = inspectionService.getById(inspectionId);
        return Result.success(inspection);
    }
    
    /**
     * 新增检查记录
     */
    @PostMapping
    @RequiresPermission({"production:inspection:add"})
    public Result<Boolean> saveInspection(@RequestBody DailyInspection inspection) {
        try {
            boolean success = inspectionService.saveInspection(inspection);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新检查记录
     */
    @PutMapping
    @RequiresPermission({"production:inspection:edit"})
    public Result<Boolean> updateInspection(@RequestBody DailyInspection inspection) {
        try {
            boolean success = inspectionService.updateInspection(inspection);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除检查记录
     */
    @DeleteMapping("/{inspectionId}")
    @RequiresPermission({"production:inspection:delete"})
    public Result<Boolean> deleteInspection(@PathVariable Long inspectionId) {
        try {
            boolean success = inspectionService.deleteInspection(inspectionId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


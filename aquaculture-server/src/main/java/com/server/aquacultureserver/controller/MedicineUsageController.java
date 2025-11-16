package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.MedicineUsage;
import com.server.aquacultureserver.service.MedicineUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用药记录控制器
 */
@RestController
@RequestMapping("/api/disease/medicine")
@CrossOrigin
public class MedicineUsageController {
    
    @Autowired
    private MedicineUsageService usageService;
    
    /**
     * 查询所有用药记录
     */
    @GetMapping("/all")
    public Result<List<MedicineUsage>> getAllUsages() {
        List<MedicineUsage> usages = usageService.getAllUsages();
        return Result.success(usages);
    }
    
    /**
     * 分页查询用药记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"disease:medicine:list"})
    public Result<Page<MedicineUsage>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long recordId,
            @RequestParam(required = false) Long preventionId,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String medicineName,
            @RequestParam(required = false) Integer status) {
        Page<MedicineUsage> page = usageService.getPage(current, size, recordId, preventionId, planId, areaId, medicineName, status);
        return Result.success(page);
    }
    
    /**
     * 根据病害记录ID查询用药记录列表
     */
    @GetMapping("/record/{recordId}")
    @RequiresPermission({"disease:medicine:list"})
    public Result<List<MedicineUsage>> getByRecordId(@PathVariable Long recordId) {
        List<MedicineUsage> usages = usageService.getByRecordId(recordId);
        return Result.success(usages);
    }
    
    /**
     * 根据防治记录ID查询用药记录列表
     */
    @GetMapping("/prevention/{preventionId}")
    @RequiresPermission({"disease:medicine:list"})
    public Result<List<MedicineUsage>> getByPreventionId(@PathVariable Long preventionId) {
        List<MedicineUsage> usages = usageService.getByPreventionId(preventionId);
        return Result.success(usages);
    }
    
    /**
     * 根据ID查询用药记录
     */
    @GetMapping("/{usageId}")
    @RequiresPermission({"disease:medicine:detail"})
    public Result<MedicineUsage> getById(@PathVariable Long usageId) {
        MedicineUsage usage = usageService.getById(usageId);
        return Result.success(usage);
    }
    
    /**
     * 新增用药记录
     */
    @PostMapping
    @RequiresPermission({"disease:medicine:add"})
    public Result<Boolean> saveUsage(@RequestBody MedicineUsage usage) {
        try {
            boolean success = usageService.saveUsage(usage);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用药记录
     */
    @PutMapping
    @RequiresPermission({"disease:medicine:edit"})
    public Result<Boolean> updateUsage(@RequestBody MedicineUsage usage) {
        try {
            boolean success = usageService.updateUsage(usage);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除用药记录
     */
    @DeleteMapping("/{usageId}")
    @RequiresPermission({"disease:medicine:delete"})
    public Result<Boolean> deleteUsage(@PathVariable Long usageId) {
        try {
            boolean success = usageService.deleteUsage(usageId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


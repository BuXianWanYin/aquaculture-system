package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.DiseasePrevention;
import com.server.aquacultureserver.service.DiseasePreventionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 病害防治记录控制器
 */
@RestController
@RequestMapping("/api/disease/prevention")
@CrossOrigin
public class DiseasePreventionController {
    
    @Autowired
    private DiseasePreventionService preventionService;
    
    /**
     * 查询所有防治记录
     */
    @GetMapping("/all")
    public Result<List<DiseasePrevention>> getAllPreventions() {
        List<DiseasePrevention> preventions = preventionService.getAllPreventions();
        return Result.success(preventions);
    }
    
    /**
     * 分页查询防治记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"disease:prevention:view"})
    public Result<Page<DiseasePrevention>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long recordId,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Integer status) {
        Page<DiseasePrevention> page = preventionService.getPage(current, size, recordId, planId, areaId, status);
        return Result.success(page);
    }
    
    /**
     * 根据病害记录ID查询防治记录列表
     */
    @GetMapping("/record/{recordId}")
    @RequiresPermission({"disease:prevention:view"})
    public Result<List<DiseasePrevention>> getByRecordId(@PathVariable Long recordId) {
        List<DiseasePrevention> preventions = preventionService.getByRecordId(recordId);
        return Result.success(preventions);
    }
    
    /**
     * 根据ID查询防治记录
     */
    @GetMapping("/{preventionId}")
    @RequiresPermission({"disease:prevention:view"})
    public Result<DiseasePrevention> getById(@PathVariable Long preventionId) {
        DiseasePrevention prevention = preventionService.getById(preventionId);
        return Result.success(prevention);
    }
    
    /**
     * 新增防治记录
     */
    @PostMapping
    @RequiresPermission({"disease:prevention:add"})
    public Result<Boolean> savePrevention(@RequestBody DiseasePrevention prevention) {
        try {
            boolean success = preventionService.savePrevention(prevention);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新防治记录
     */
    @PutMapping
    @RequiresPermission({"disease:prevention:edit"})
    public Result<Boolean> updatePrevention(@RequestBody DiseasePrevention prevention) {
        try {
            boolean success = preventionService.updatePrevention(prevention);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除防治记录
     */
    @DeleteMapping("/{preventionId}")
    @RequiresPermission({"disease:prevention:delete"})
    public Result<Boolean> deletePrevention(@PathVariable Long preventionId) {
        try {
            boolean success = preventionService.deletePrevention(preventionId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


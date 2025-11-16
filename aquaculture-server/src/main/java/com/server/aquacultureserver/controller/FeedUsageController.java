package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.FeedUsage;
import com.server.aquacultureserver.service.FeedUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 饲料使用记录控制器
 */
@RestController
@RequestMapping("/api/feed/usage")
@CrossOrigin
public class FeedUsageController {
    
    @Autowired
    private FeedUsageService usageService;
    
    /**
     * 查询所有使用记录
     */
    @GetMapping("/all")
    public Result<List<FeedUsage>> getAllUsages() {
        List<FeedUsage> usages = usageService.getAllUsages();
        return Result.success(usages);
    }
    
    /**
     * 分页查询使用记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"feed:usage:view"})
    public Result<Page<FeedUsage>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String feedName,
            @RequestParam(required = false) String feedType,
            @RequestParam(required = false) Integer status) {
        Page<FeedUsage> page = usageService.getPage(current, size, planId, areaId, feedName, feedType, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询使用记录
     */
    @GetMapping("/{usageId}")
    @RequiresPermission({"feed:usage:view"})
    public Result<FeedUsage> getById(@PathVariable Long usageId) {
        FeedUsage usage = usageService.getById(usageId);
        return Result.success(usage);
    }
    
    /**
     * 新增使用记录
     */
    @PostMapping
    @RequiresPermission({"feed:usage:add"})
    public Result<Boolean> saveUsage(@RequestBody FeedUsage usage) {
        try {
            boolean success = usageService.saveUsage(usage);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新使用记录
     */
    @PutMapping
    @RequiresPermission({"feed:usage:edit"})
    public Result<Boolean> updateUsage(@RequestBody FeedUsage usage) {
        try {
            boolean success = usageService.updateUsage(usage);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除使用记录
     */
    @DeleteMapping("/{usageId}")
    @RequiresPermission({"feed:usage:delete"})
    public Result<Boolean> deleteUsage(@PathVariable Long usageId) {
        try {
            boolean success = usageService.deleteUsage(usageId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


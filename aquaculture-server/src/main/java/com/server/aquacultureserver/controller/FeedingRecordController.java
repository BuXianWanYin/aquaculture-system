package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.FeedingRecord;
import com.server.aquacultureserver.service.FeedingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投喂记录控制器
 */
@RestController
@RequestMapping("/api/production/feeding")
@CrossOrigin
public class FeedingRecordController {
    
    @Autowired
    private FeedingRecordService recordService;
    
    /**
     * 查询所有投喂记录
     */
    @GetMapping("/all")
    public Result<List<FeedingRecord>> getAllRecords() {
        List<FeedingRecord> records = recordService.getAllRecords();
        return Result.success(records);
    }
    
    /**
     * 分页查询投喂记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"production:feeding:list"})
    public Result<Page<FeedingRecord>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String feedName,
            @RequestParam(required = false) Integer status) {
        Page<FeedingRecord> page = recordService.getPage(current, size, planId, areaId, feedName, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询投喂记录
     */
    @GetMapping("/{recordId}")
    @RequiresPermission({"production:feeding:detail"})
    public Result<FeedingRecord> getById(@PathVariable Long recordId) {
        FeedingRecord record = recordService.getById(recordId);
        return Result.success(record);
    }
    
    /**
     * 新增投喂记录
     */
    @PostMapping
    @RequiresPermission({"production:feeding:add"})
    public Result<Boolean> saveRecord(@RequestBody FeedingRecord record) {
        try {
            boolean success = recordService.saveRecord(record);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新投喂记录
     */
    @PutMapping
    @RequiresPermission({"production:feeding:edit"})
    public Result<Boolean> updateRecord(@RequestBody FeedingRecord record) {
        try {
            boolean success = recordService.updateRecord(record);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除投喂记录
     */
    @DeleteMapping("/{recordId}")
    @RequiresPermission({"production:feeding:delete"})
    public Result<Boolean> deleteRecord(@PathVariable Long recordId) {
        try {
            boolean success = recordService.deleteRecord(recordId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


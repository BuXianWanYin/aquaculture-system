package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.SalesRecord;
import com.server.aquacultureserver.service.SalesRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 销售记录控制器
 */
@RestController
@RequestMapping("/api/sales/record")
@CrossOrigin
public class SalesRecordController {
    
    @Autowired
    private SalesRecordService recordService;
    
    /**
     * 查询所有销售记录
     */
    @GetMapping("/all")
    public Result<List<SalesRecord>> getAllRecords() {
        List<SalesRecord> records = recordService.getAllRecords();
        return Result.success(records);
    }
    
    /**
     * 分页查询销售记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"sales:record:list"})
    public Result<Page<SalesRecord>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Long breedId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer paymentStatus,
            @RequestParam(required = false) Integer status) {
        Page<SalesRecord> page = recordService.getPage(current, size, planId, areaId, breedId, customerId, paymentStatus, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询销售记录
     */
    @GetMapping("/{salesId}")
    @RequiresPermission({"sales:record:detail"})
    public Result<SalesRecord> getById(@PathVariable Long salesId) {
        SalesRecord record = recordService.getById(salesId);
        return Result.success(record);
    }
    
    /**
     * 新增销售记录
     */
    @PostMapping
    @RequiresPermission({"sales:record:add"})
    public Result<Boolean> saveRecord(@RequestBody SalesRecord record) {
        try {
            boolean success = recordService.saveRecord(record);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新销售记录
     */
    @PutMapping
    @RequiresPermission({"sales:record:edit"})
    public Result<Boolean> updateRecord(@RequestBody SalesRecord record) {
        try {
            boolean success = recordService.updateRecord(record);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除销售记录
     */
    @DeleteMapping("/{salesId}")
    @RequiresPermission({"sales:record:delete"})
    public Result<Boolean> deleteRecord(@PathVariable Long salesId) {
        try {
            boolean success = recordService.deleteRecord(salesId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 统计销售收入
     */
    @GetMapping("/statistics")
    @RequiresPermission({"sales:record:list"})
    public Result<Map<String, Object>> getSalesStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Long breedId) {
        Map<String, Object> statistics = recordService.getSalesStatistics(startDate, endDate, areaId, breedId);
        return Result.success(statistics);
    }
}


package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.DiseaseRecord;
import com.server.aquacultureserver.service.DiseaseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 病害记录控制器
 */
@RestController
@RequestMapping("/api/disease/record")
@CrossOrigin
public class DiseaseRecordController {
    
    @Autowired
    private DiseaseRecordService recordService;
    
    /**
     * 查询所有病害记录
     */
    @GetMapping("/all")
    public Result<List<DiseaseRecord>> getAllRecords() {
        List<DiseaseRecord> records = recordService.getAllRecords();
        return Result.success(records);
    }
    
    /**
     * 分页查询病害记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"disease:record:list"})
    public Result<Page<DiseaseRecord>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String diseaseName,
            @RequestParam(required = false) String diseaseType,
            @RequestParam(required = false) Integer status) {
        Page<DiseaseRecord> page = recordService.getPage(current, size, planId, areaId, diseaseName, diseaseType, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询病害记录
     */
    @GetMapping("/{recordId}")
    @RequiresPermission({"disease:record:detail"})
    public Result<DiseaseRecord> getById(@PathVariable Long recordId) {
        DiseaseRecord record = recordService.getById(recordId);
        return Result.success(record);
    }
    
    /**
     * 新增病害记录
     */
    @PostMapping
    @RequiresPermission({"disease:record:add"})
    public Result<Boolean> saveRecord(@RequestBody DiseaseRecord record) {
        try {
            boolean success = recordService.saveRecord(record);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新病害记录
     */
    @PutMapping
    @RequiresPermission({"disease:record:edit"})
    public Result<Boolean> updateRecord(@RequestBody DiseaseRecord record) {
        try {
            boolean success = recordService.updateRecord(record);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除病害记录
     */
    @DeleteMapping("/{recordId}")
    @RequiresPermission({"disease:record:delete"})
    public Result<Boolean> deleteRecord(@PathVariable Long recordId) {
        try {
            boolean success = recordService.deleteRecord(recordId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


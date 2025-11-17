package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.SysOperLog;
import com.server.aquacultureserver.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/operLog")
@CrossOrigin
public class SysOperLogController {
    
    @Autowired
    private SysOperLogService operLogService;
    
    /**
     * 分页查询操作日志列表
     */
    @GetMapping("/page")
    @RequiresPermission({"log:list"}) // 需要日志列表查看权限
    public Result<Page<SysOperLog>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Page<SysOperLog> page = operLogService.getPage(current, size, userId, module, operType, keyword, startTime, endTime);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询操作日志详情
     */
    @GetMapping("/{logId}")
    @RequiresPermission({"log:detail"}) // 需要日志详情查看权限
    public Result<SysOperLog> getById(@PathVariable Long logId) {
        SysOperLog log = operLogService.getById(logId);
        return Result.success(log);
    }
    
    /**
     * 删除操作日志
     */
    @DeleteMapping("/{logId}")
    @RequiresPermission({"log:delete"}) // 需要日志删除权限
    public Result<Boolean> deleteLog(@PathVariable Long logId) {
        try {
            boolean success = operLogService.deleteLog(logId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除操作日志
     */
    @DeleteMapping("/batch")
    @RequiresPermission({"log:delete"}) // 需要日志删除权限
    public Result<Boolean> deleteBatch(@RequestBody Long[] logIds) {
        try {
            boolean success = operLogService.deleteBatch(logIds);
            return Result.success("批量删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 清空操作日志
     */
    @DeleteMapping("/clear")
    @RequiresPermission({"log:clear"}) // 需要日志清空权限
    public Result<Boolean> clearLog() {
        try {
            boolean success = operLogService.clearLog();
            return Result.success("清空成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


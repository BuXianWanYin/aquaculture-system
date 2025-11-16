package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysOperLog;

import java.time.LocalDateTime;

/**
 * 操作日志服务接口
 */
public interface SysOperLogService {
    
    /**
     * 分页查询操作日志列表
     */
    Page<SysOperLog> getPage(Integer current, Integer size, 
                             Long userId, String module, 
                             String operType, String keyword,
                             LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据ID查询操作日志详情
     */
    SysOperLog getById(Long logId);
    
    /**
     * 保存操作日志
     */
    boolean saveLog(SysOperLog log);
    
    /**
     * 删除操作日志
     */
    boolean deleteLog(Long logId);
    
    /**
     * 批量删除操作日志
     */
    boolean deleteBatch(Long[] logIds);
    
    /**
     * 清空操作日志（仅管理员）
     */
    boolean clearLog();
}


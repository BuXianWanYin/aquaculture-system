package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysOperLog;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.mapper.SysOperLogMapper;
import com.server.aquacultureserver.mapper.SysUserMapper;
import com.server.aquacultureserver.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 操作日志服务实现类
 */
@Service
public class SysOperLogServiceImpl implements SysOperLogService {
    
    @Autowired
    private SysOperLogMapper operLogMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Override
    public Page<SysOperLog> getPage(Integer current, Integer size, 
                                     Long userId, String module, 
                                     String operType, String keyword,
                                     LocalDateTime startTime, LocalDateTime endTime) {
        Page<SysOperLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(SysOperLog::getUserId, userId);
        }
        if (module != null && !module.isEmpty()) {
            wrapper.eq(SysOperLog::getModule, module);
        }
        if (operType != null && !operType.isEmpty()) {
            wrapper.eq(SysOperLog::getOperType, operType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysOperLog::getOperContent, keyword)
                    .or().like(SysOperLog::getModule, keyword));
        }
        if (startTime != null) {
            wrapper.ge(SysOperLog::getOperTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(SysOperLog::getOperTime, endTime);
        }
        
        wrapper.orderByDesc(SysOperLog::getOperTime);
        Page<SysOperLog> result = operLogMapper.selectPage(page, wrapper);
        
        // 填充用户信息
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            for (SysOperLog log : result.getRecords()) {
                if (log.getUserId() != null) {
                    SysUser user = userMapper.selectById(log.getUserId());
                    if (user != null) {
                        log.setUsername(user.getUsername());
                        log.setRealName(user.getRealName());
                    }
                }
            }
        }
        
        return result;
    }
    
    @Override
    public SysOperLog getById(Long logId) {
        SysOperLog log = operLogMapper.selectById(logId);
        if (log != null && log.getUserId() != null) {
            SysUser user = userMapper.selectById(log.getUserId());
            if (user != null) {
                log.setUsername(user.getUsername());
                log.setRealName(user.getRealName());
            }
        }
        return log;
    }
    
    @Override
    public boolean saveLog(SysOperLog log) {
        if (log.getOperTime() == null) {
            log.setOperTime(LocalDateTime.now());
        }
        return operLogMapper.insert(log) > 0;
    }
    
    @Override
    public boolean deleteLog(Long logId) {
        return operLogMapper.deleteById(logId) > 0;
    }
    
    @Override
    public boolean deleteBatch(Long[] logIds) {
        for (Long logId : logIds) {
            operLogMapper.deleteById(logId);
        }
        return true;
    }
    
    @Override
    public boolean clearLog() {
        operLogMapper.delete(null);
        return true;
    }
}


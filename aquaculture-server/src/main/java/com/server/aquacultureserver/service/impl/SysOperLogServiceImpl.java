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
    
    /**
     * 分页查询操作日志
     * 查询结果会自动填充用户信息（用户名、真实姓名）
     * @param current 当前页码
     * @param size 每页大小
     * @param userId 用户ID（精确查询）
     * @param module 操作模块（精确查询）
     * @param operType 操作类型（精确查询）
     * @param keyword 关键词（模糊查询操作内容或模块）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果（包含用户信息）
     */
    @Override
    public Page<SysOperLog> getPage(Integer current, Integer size, 
                                     Long userId, String module, 
                                     String operType, String keyword,
                                     LocalDateTime startTime, LocalDateTime endTime) {
        Page<SysOperLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        
        // 用户ID精确查询
        if (userId != null) {
            wrapper.eq(SysOperLog::getUserId, userId);
        }
        // 操作模块精确查询
        if (module != null && !module.isEmpty()) {
            wrapper.eq(SysOperLog::getModule, module);
        }
        // 操作类型精确查询
        if (operType != null && !operType.isEmpty()) {
            wrapper.eq(SysOperLog::getOperType, operType);
        }
        // 关键词模糊查询（操作内容或模块）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysOperLog::getOperContent, keyword)
                    .or().like(SysOperLog::getModule, keyword));
        }
        // 时间范围查询
        if (startTime != null) {
            wrapper.ge(SysOperLog::getOperTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(SysOperLog::getOperTime, endTime);
        }
        
        // 按操作时间倒序排列
        wrapper.orderByDesc(SysOperLog::getOperTime);
        Page<SysOperLog> result = operLogMapper.selectPage(page, wrapper);
        
        // 填充用户信息（用户名、真实姓名）
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
    
    /**
     * 根据ID查询操作日志
     * 查询结果会自动填充用户信息（用户名、真实姓名）
     * @param logId 日志ID
     * @return 操作日志（包含用户信息）
     */
    @Override
    public SysOperLog getById(Long logId) {
        SysOperLog log = operLogMapper.selectById(logId);
        if (log != null && log.getUserId() != null) {
            // 填充用户信息
            SysUser user = userMapper.selectById(log.getUserId());
            if (user != null) {
                log.setUsername(user.getUsername());
                log.setRealName(user.getRealName());
            }
        }
        return log;
    }
    
    /**
     * 保存操作日志
     * @param log 操作日志
     * @return 是否成功
     */
    @Override
    public boolean saveLog(SysOperLog log) {
        // 如果未设置操作时间，则使用当前时间
        if (log.getOperTime() == null) {
            log.setOperTime(LocalDateTime.now());
        }
        return operLogMapper.insert(log) > 0;
    }
    
    /**
     * 删除操作日志（物理删除）
     * @param logId 日志ID
     * @return 是否成功
     */
    @Override
    public boolean deleteLog(Long logId) {
        return operLogMapper.deleteById(logId) > 0;
    }
    
    /**
     * 批量删除操作日志（物理删除）
     * @param logIds 日志ID数组
     * @return 是否成功
     */
    @Override
    public boolean deleteBatch(Long[] logIds) {
        for (Long logId : logIds) {
            operLogMapper.deleteById(logId);
        }
        return true;
    }
    
    /**
     * 清空所有操作日志（物理删除）
     * @return 是否成功
     */
    @Override
    public boolean clearLog() {
        operLogMapper.delete(null);
        return true;
    }
}


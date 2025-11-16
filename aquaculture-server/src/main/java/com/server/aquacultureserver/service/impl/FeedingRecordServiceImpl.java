package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.FeedingRecord;
import com.server.aquacultureserver.mapper.FeedingRecordMapper;
import com.server.aquacultureserver.service.FeedingRecordService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 投喂记录服务实现类
 */
@Service
public class FeedingRecordServiceImpl implements FeedingRecordService {
    
    @Autowired
    private FeedingRecordMapper recordMapper;
    
    @Override
    public List<FeedingRecord> getAllRecords() {
        LambdaQueryWrapper<FeedingRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的投喂记录
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(FeedingRecord::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下所有区域的投喂记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(FeedingRecord::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.eq(FeedingRecord::getStatus, 1);
        wrapper.orderByDesc(FeedingRecord::getCreateTime);
        return recordMapper.selectList(wrapper);
    }
    
    @Override
    public FeedingRecord getById(Long recordId) {
        return recordMapper.selectById(recordId);
    }
    
    @Override
    public Page<FeedingRecord> getPage(Integer current, Integer size, Long planId, Long areaId, String feedName, Integer status) {
        Page<FeedingRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<FeedingRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的投喂记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(FeedingRecord::getAreaId, userAreaId);
            } else {
                return page;
            }
        }
        // 部门管理员只能查看其部门下所有区域的投喂记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(FeedingRecord::getAreaId, areaIds);
            } else {
                return page;
            }
        }
        
        if (planId != null) {
            wrapper.eq(FeedingRecord::getPlanId, planId);
        }
        if (areaId != null) {
            wrapper.eq(FeedingRecord::getAreaId, areaId);
        }
        if (feedName != null && !feedName.isEmpty()) {
            wrapper.like(FeedingRecord::getFeedName, feedName);
        }
        if (status != null) {
            wrapper.eq(FeedingRecord::getStatus, status);
        } else {
            wrapper.eq(FeedingRecord::getStatus, 1);
        }
        
        wrapper.orderByDesc(FeedingRecord::getCreateTime);
        return recordMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveRecord(FeedingRecord record) {
        // 普通操作员只能创建自己区域的投喂记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法创建投喂记录");
            }
            record.setAreaId(userAreaId);
        }
        // 部门管理员只能创建其部门下区域的投喂记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法创建投喂记录");
            }
            if (record.getAreaId() == null || !areaIds.contains(record.getAreaId())) {
                throw new RuntimeException("您只能创建本部门下区域的投喂记录");
            }
        }
        
        // 新记录默认状态为正常
        if (record.getStatus() == null) {
            record.setStatus(1);
        }
        
        // 设置创建时间和创建人
        if (record.getCreateTime() == null) {
            record.setCreateTime(LocalDateTime.now());
        }
        if (record.getCreatorId() == null) {
            record.setCreatorId(UserContext.getCurrentUserId());
        }
        
        return recordMapper.insert(record) > 0;
    }
    
    @Override
    public boolean updateRecord(FeedingRecord record) {
        return recordMapper.updateById(record) > 0;
    }
    
    @Override
    public boolean deleteRecord(Long recordId) {
        // 软删除，将状态设置为0
        FeedingRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("投喂记录不存在");
        }
        record.setStatus(0);
        return recordMapper.updateById(record) > 0;
    }
    
    @Override
    public long count() {
        LambdaQueryWrapper<FeedingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedingRecord::getStatus, 1);
        return recordMapper.selectCount(wrapper);
    }
}

